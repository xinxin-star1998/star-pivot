package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileMediaType;
import com.star.pivot.file.domain.dto.SysFileShareCreateDTO;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.entity.SysFileShare;
import com.star.pivot.file.domain.vo.SysFileSharePublicVo;
import com.star.pivot.file.domain.vo.SysFileShareVo;
import com.star.pivot.file.domain.vo.SysFileWatermarkVo;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.mapper.SysFileShareMapper;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.file.service.ISysFileShareService;
import com.star.pivot.file.service.ISysFileWatermarkService;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.security.context.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysFileShareServiceImpl extends ServiceImpl<SysFileShareMapper, SysFileShare>
        implements ISysFileShareService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghjkmnpqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SysFileShareMapper sysFileShareMapper;
    private final SysFileMapper sysFileMapper;
    private final FileStorageService fileStorageService;
    private final FileCenterProperties fileCenterProperties;
    private final ISysFileAuditService sysFileAuditService;
    private final ISysFileWatermarkService sysFileWatermarkService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileShareVo create(SysFileShareCreateDTO dto, DataScope dataScope, String publicBaseUrl) {
        SysFile file = requireAccessibleFile(dto.getFileId(), dataScope);

        SysFileShare share = new SysFileShare();
        share.setFileId(file.getFileId());
        share.setShareCode(generateUniqueCode());
        if (StringUtils.hasText(dto.getPassword())) {
            share.setPasswordHash(SecurityUtils.encryptPassword(dto.getPassword()));
        }
        share.setExpireTime(dto.getExpireTime());
        share.setMaxViews(dto.getMaxViews());
        share.setViewCount(0);
        share.setAllowDownload(Boolean.FALSE.equals(dto.getAllowDownload()) ? "0" : "1");
        share.setStatus("0");
        share.setCreateBy(SecurityContextUtils.getUsername());
        share.setCreateByUserId(SecurityContextUtils.getUserId());
        share.setCreateTime(LocalDateTime.now());
        share.setRemark(dto.getRemark());
        save(share);
        sysFileAuditService.record(FileAuditAction.SHARE, file.getFileId(), file.getFileName(),
                "创建分享 " + share.getShareCode());
        return toVo(share, file, publicBaseUrl);
    }

    @Override
    public List<SysFileShareVo> listByFile(Long fileId, DataScope dataScope, String publicBaseUrl) {
        requireAccessibleFile(fileId, dataScope);
        SysFile file = sysFileMapper.selectById(fileId);
        return list(new LambdaQueryWrapper<SysFileShare>()
                .eq(SysFileShare::getFileId, fileId)
                .eq(SysFileShare::getStatus, "0")
                .orderByDesc(SysFileShare::getCreateTime))
                .stream()
                .map(s -> toVo(s, file, publicBaseUrl))
                .collect(Collectors.toList());
    }

    @Override
    public List<SysFileShareVo> listMine(String publicBaseUrl) {
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            return List.of();
        }
        List<SysFileShare> shares = list(new LambdaQueryWrapper<SysFileShare>()
                .eq(SysFileShare::getCreateByUserId, userId)
                .eq(SysFileShare::getStatus, "0")
                .orderByDesc(SysFileShare::getCreateTime));
        if (shares.isEmpty()) {
            return List.of();
        }
        Set<Long> fileIds = shares.stream().map(SysFileShare::getFileId).collect(Collectors.toSet());
        Map<Long, SysFile> fileMap = sysFileMapper.selectBatchIds(fileIds).stream()
                .collect(Collectors.toMap(SysFile::getFileId, f -> f, (a, b) -> a));
        return shares.stream()
                .map(s -> toVo(s, fileMap.get(s.getFileId()), publicBaseUrl))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long shareId) {
        SysFileShare share = getById(shareId);
        AssertUtils.notNull(share, ErrorCode.NOT_FOUND, "分享不存在");
        Long userId = SecurityContextUtils.getUserId();
        if (userId != null && !userId.equals(share.getCreateByUserId())
                && !SecurityContextUtils.hasAuthority("file:resource:purge")) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权取消该分享");
        }
        share.setStatus("1");
        share.setUpdateBy(SecurityContextUtils.getUsername());
        share.setUpdateTime(LocalDateTime.now());
        updateById(share);
        SysFile file = sysFileMapper.selectById(share.getFileId());
        sysFileAuditService.record(FileAuditAction.SHARE_REVOKE, share.getFileId(),
                file != null ? file.getFileName() : null, "取消分享 " + share.getShareCode());
    }

    @Override
    public SysFileSharePublicVo meta(String shareCode) {
        SysFileShare share = getActiveShare(shareCode);
        SysFile file = sysFileMapper.selectById(share.getFileId());
        if (file == null || !FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件不存在或已删除");
        }
        boolean expired = isExpired(share);
        return SysFileSharePublicVo.builder()
                .shareCode(share.getShareCode())
                .fileName(file.getFileName())
                .mediaType(file.getMediaType())
                .mediaTypeLabel(FileMediaType.of(file.getMediaType()).getLabel())
                .fileSize(file.getFileSize())
                .fileExt(file.getFileExt())
                .hasPassword(StringUtils.hasText(share.getPasswordHash()))
                .expired(expired)
                .allowDownload("1".equals(share.getAllowDownload()))
                .unlocked(!StringUtils.hasText(share.getPasswordHash()) && !expired)
                .previewMode(resolvePreviewMode(file))
                .watermark(guestWatermark())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileSharePublicVo unlock(String shareCode, String password) {
        SysFileShare share = getActiveShare(shareCode);
        if (isExpired(share)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "分享链接已过期");
        }
        if (share.getMaxViews() != null && share.getViewCount() != null
                && share.getViewCount() >= share.getMaxViews()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "分享链接访问次数已达上限");
        }
        if (StringUtils.hasText(share.getPasswordHash())) {
            if (!StringUtils.hasText(password)
                    || !SecurityUtils.matchesPassword(password, share.getPasswordHash())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "访问密码错误");
            }
        }

        SysFile file = sysFileMapper.selectById(share.getFileId());
        if (file == null || !FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件不存在或已删除");
        }

        share.setViewCount((share.getViewCount() == null ? 0 : share.getViewCount()) + 1);
        updateById(share);

        String url;
        try {
            url = fileStorageService.getPresignedUrl(file.getObjectName());
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "获取文件链接失败");
        }

        String mode = resolvePreviewMode(file);
        String viewerUrl = null;
        if ("office".equals(mode)) {
            viewerUrl = buildOfficeViewerUrl(url);
        } else if ("pdf".equals(mode) || "image".equals(mode) || "video".equals(mode) || "audio".equals(mode)) {
            viewerUrl = url;
        }

        return SysFileSharePublicVo.builder()
                .shareCode(share.getShareCode())
                .fileName(file.getFileName())
                .mediaType(file.getMediaType())
                .mediaTypeLabel(FileMediaType.of(file.getMediaType()).getLabel())
                .fileSize(file.getFileSize())
                .fileExt(file.getFileExt())
                .hasPassword(StringUtils.hasText(share.getPasswordHash()))
                .expired(false)
                .allowDownload("1".equals(share.getAllowDownload()))
                .unlocked(true)
                .url(url)
                .previewMode(mode)
                .viewerUrl(viewerUrl)
                .watermark(guestWatermark())
                .build();
    }

    private SysFileWatermarkVo guestWatermark() {
        return sysFileWatermarkService.resolveConfig(null);
    }

    private SysFile requireAccessibleFile(Long fileId, DataScope dataScope) {
        SysFile file = sysFileMapper.selectOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .eq(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        if (!FileDataScopeSupport.isAccessible(file, dataScope)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权分享该文件");
        }
        return file;
    }

    private SysFileShare getActiveShare(String shareCode) {
        AssertUtils.notEmpty(shareCode, ErrorCode.PARAM_INVALID, "分享码不能为空");
        SysFileShare share = getOne(new LambdaQueryWrapper<SysFileShare>()
                .eq(SysFileShare::getShareCode, shareCode)
                .eq(SysFileShare::getStatus, "0"));
        AssertUtils.notNull(share, ErrorCode.NOT_FOUND, "分享不存在或已取消");
        return share;
    }

    private boolean isExpired(SysFileShare share) {
        return share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now());
    }

    private String generateUniqueCode() {
        for (int i = 0; i < 8; i++) {
            String code = randomCode(8);
            long exists = count(new LambdaQueryWrapper<SysFileShare>().eq(SysFileShare::getShareCode, code));
            if (exists == 0) {
                return code;
            }
        }
        return randomCode(12);
    }

    private String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private SysFileShareVo toVo(SysFileShare share, SysFile file, String publicBaseUrl) {
        String base = StringUtils.hasText(publicBaseUrl) ? publicBaseUrl.replaceAll("/+$", "") : "";
        return SysFileShareVo.builder()
                .shareId(share.getShareId())
                .fileId(share.getFileId())
                .shareCode(share.getShareCode())
                .shareUrl(base + "/s/" + share.getShareCode())
                .hasPassword(StringUtils.hasText(share.getPasswordHash()))
                .expireTime(share.getExpireTime())
                .maxViews(share.getMaxViews())
                .viewCount(share.getViewCount())
                .allowDownload("1".equals(share.getAllowDownload()))
                .status(share.getStatus())
                .fileName(file != null ? file.getFileName() : null)
                .mediaType(file != null ? file.getMediaType() : null)
                .createBy(share.getCreateBy())
                .createTime(share.getCreateTime())
                .build();
    }

    private String resolvePreviewMode(SysFile file) {
        FileMediaType mediaType = FileMediaType.of(file.getMediaType());
        String ext = file.getFileExt();
        return switch (mediaType) {
            case IMAGE -> "image";
            case VIDEO -> "video";
            case AUDIO -> "audio";
            case DOCUMENT -> {
                if ("pdf".equalsIgnoreCase(ext)) {
                    yield "pdf";
                }
                if (isOfficeExt(ext)) {
                    yield "office";
                }
                yield "download";
            }
            default -> "download";
        };
    }

    private boolean isOfficeExt(String fileExt) {
        if (!StringUtils.hasText(fileExt) || fileCenterProperties.getPreview() == null) {
            return false;
        }
        Set<String> exts = fileCenterProperties.getPreview().getOfficeExtensions();
        return exts != null && exts.contains(fileExt.toLowerCase(Locale.ROOT));
    }

    private String buildOfficeViewerUrl(String fileUrl) {
        FileCenterProperties.Preview preview = fileCenterProperties.getPreview();
        if (preview == null || !StringUtils.hasText(fileUrl)) {
            return null;
        }
        try {
            String encoded = java.net.URLEncoder.encode(fileUrl, java.nio.charset.StandardCharsets.UTF_8);
            if ("kkfileview".equalsIgnoreCase(preview.getOfficeViewer())
                    && StringUtils.hasText(preview.getKkfileviewBaseUrl())) {
                String base = preview.getKkfileviewBaseUrl().replaceAll("/+$", "");
                String b64 = Base64.getEncoder()
                        .encodeToString(fileUrl.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                return base + "/onlinePreview?url="
                        + java.net.URLEncoder.encode(b64, java.nio.charset.StandardCharsets.UTF_8);
            }
            return "https://view.officeapps.live.com/op/embed.aspx?src=" + encoded;
        } catch (Exception e) {
            return null;
        }
    }
}
