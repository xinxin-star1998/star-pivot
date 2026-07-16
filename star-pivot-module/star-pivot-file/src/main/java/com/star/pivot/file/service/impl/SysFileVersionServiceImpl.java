package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileCategory;
import com.star.pivot.file.constant.FileMediaType;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.entity.SysFileFolder;
import com.star.pivot.file.domain.entity.SysFileVersion;
import com.star.pivot.file.domain.vo.SysFileVersionVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.mapper.SysFileFolderMapper;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.mapper.SysFileVersionMapper;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.file.service.ISysFileVersionService;
import com.star.pivot.file.support.FileCenterUploadHelper;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.file.support.FileHashUtils;
import com.star.pivot.file.support.FileMediaTypeResolver;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileVersionServiceImpl implements ISysFileVersionService {

    private final SysFileMapper sysFileMapper;
    private final SysFileFolderMapper sysFileFolderMapper;
    private final SysFileVersionMapper versionMapper;
    private final FileCenterUploadHelper fileCenterUploadHelper;
    private final FileMediaTypeResolver fileMediaTypeResolver;
    private final FileStorageService fileStorageService;
    private final FileCenterProperties fileCenterProperties;
    private final ISysFileAuditService sysFileAuditService;

    @Override
    public List<SysFileVersionVo> listVersions(Long fileId, DataScope dataScope) {
        SysFile file = requireAccessibleActive(fileId, dataScope);
        List<SysFileVersionVo> result = new ArrayList<>();

        SysFileVersionVo current = new SysFileVersionVo();
        current.setFileId(file.getFileId());
        current.setVersionNo(nextVersionNo(fileId));
        current.setFileName(file.getFileName());
        current.setFileSize(file.getFileSize());
        current.setFileHash(file.getFileHash());
        current.setContentType(file.getContentType());
        current.setCurrent(true);
        current.setCreateBy(file.getUpdateBy() != null ? file.getUpdateBy() : file.getCreateBy());
        current.setCreateTime(file.getUpdateTime() != null ? file.getUpdateTime() : file.getCreateTime());
        current.setRemark("当前版本");
        if (FileMediaType.IMAGE.getCode().equals(file.getMediaType()) && StringUtils.hasText(file.getObjectName())) {
            try {
                current.setDisplayUrl(fileStorageService.getPresignedUrl(file.getObjectName()));
            } catch (Exception ignored) {
                current.setDisplayUrl(fileStorageService.getPermanentUrl(file.getObjectName()));
            }
        }
        result.add(current);

        List<SysFileVersion> history = versionMapper.selectList(new LambdaQueryWrapper<SysFileVersion>()
                .eq(SysFileVersion::getFileId, fileId)
                .orderByDesc(SysFileVersion::getVersionNo));
        for (SysFileVersion v : history) {
            result.add(toVo(v, false));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo uploadVersion(Long fileId, MultipartFile file, String remark, DataScope dataScope) {
        AssertUtils.notNull(file, ErrorCode.PARAM_INVALID, "文件不能为空");
        if (file.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件不能为空");
        }
        SysFile current = requireAccessibleActive(fileId, dataScope);
        SysFileFolder folder = sysFileFolderMapper.selectById(current.getFolderId());
        AssertUtils.notNull(folder, ErrorCode.NOT_FOUND, "文件夹不存在");

        FileMediaType mediaType = fileMediaTypeResolver.resolve(file);
        validateFileSize(file.getSize(), mediaType);

        archiveCurrent(current, remark);

        byte[] bytes;
        String fileHash;
        try {
            bytes = file.getBytes();
            fileHash = FileHashUtils.sha256Hex(bytes);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "读取文件失败");
        }

        FileCategory category = FileCategory.of(folder.getCategory());
        String suffix = resolveSuffix(file.getOriginalFilename());
        String objectName = fileCenterUploadHelper.buildObjectName(
                category.getObjectPathSegment(), folder.getFolderId(), suffix);
        try {
            fileStorageService.uploadFileInternal(file, objectName);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "上传新版本失败: " + e.getMessage());
        }

        String displayName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename() : current.getFileName();
        sysFileMapper.update(null, new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .set(SysFile::getFileName, displayName)
                .set(SysFile::getFileExt, fileMediaTypeResolver.extractExtension(displayName))
                .set(SysFile::getContentType, file.getContentType())
                .set(SysFile::getMediaType, mediaType.getCode())
                .set(SysFile::getFileSize, file.getSize())
                .set(SysFile::getObjectName, objectName)
                .set(SysFile::getFileHash, fileHash)
                .set(SysFile::getUpdateBy, SecurityContextUtils.getUsername())
                .set(SysFile::getUpdateTime, LocalDateTime.now())
                .set(StringUtils.hasText(remark), SysFile::getRemark, remark));

        SysFile updated = sysFileMapper.selectById(fileId);
        sysFileAuditService.record(FileAuditAction.VERSION_UPLOAD, fileId, updated.getFileName(), "上传新版本");
        SysFileVo vo = new SysFileVo();
        BeanUtils.copyProperties(updated, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo restoreVersion(Long fileId, Long versionId, DataScope dataScope) {
        SysFile current = requireAccessibleActive(fileId, dataScope);
        SysFileVersion version = versionMapper.selectById(versionId);
        AssertUtils.notNull(version, ErrorCode.NOT_FOUND, "版本不存在");
        if (!fileId.equals(version.getFileId())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "版本不属于该文件");
        }

        archiveCurrent(current, "恢复前自动归档");

        sysFileMapper.update(null, new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .set(SysFile::getFileName, version.getFileName())
                .set(SysFile::getContentType, version.getContentType())
                .set(SysFile::getFileSize, version.getFileSize())
                .set(SysFile::getObjectName, version.getObjectName())
                .set(SysFile::getFileHash, version.getFileHash())
                .set(SysFile::getStorageProvider, version.getStorageProvider())
                .set(SysFile::getFileExt, fileMediaTypeResolver.extractExtension(version.getFileName()))
                .set(SysFile::getUpdateBy, SecurityContextUtils.getUsername())
                .set(SysFile::getUpdateTime, LocalDateTime.now())
                .set(SysFile::getRemark, "已恢复到版本 v" + version.getVersionNo()));

        // 恢复后历史快照仍保留（同一 object 可被引用计数保护）
        SysFile updated = sysFileMapper.selectById(fileId);
        sysFileAuditService.record(FileAuditAction.VERSION_RESTORE, fileId, updated.getFileName(),
                "恢复到版本 v" + version.getVersionNo());
        SysFileVo vo = new SysFileVo();
        BeanUtils.copyProperties(updated, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(Long fileId, Long versionId, DataScope dataScope) {
        requireAccessibleActive(fileId, dataScope);
        SysFileVersion version = versionMapper.selectById(versionId);
        AssertUtils.notNull(version, ErrorCode.NOT_FOUND, "版本不存在");
        if (!fileId.equals(version.getFileId())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "版本不属于该文件");
        }
        String objectName = version.getObjectName();
        versionMapper.deleteById(versionId);
        tryDeleteObject(objectName, null, versionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purgeVersionsOfFile(Long fileId, String currentObjectName) {
        List<SysFileVersion> versions = versionMapper.selectList(new LambdaQueryWrapper<SysFileVersion>()
                .eq(SysFileVersion::getFileId, fileId));
        versionMapper.deleteByFileId(fileId);
        for (SysFileVersion v : versions) {
            tryDeleteObject(v.getObjectName(), fileId, v.getVersionId());
        }
        // 当前对象由调用方按 sys_file 引用计数处理
    }

    private void archiveCurrent(SysFile current, String remark) {
        if (!StringUtils.hasText(current.getObjectName())) {
            return;
        }
        int versionNo = nextVersionNo(current.getFileId());
        SysFileVersion archived = new SysFileVersion();
        archived.setFileId(current.getFileId());
        archived.setVersionNo(versionNo);
        archived.setObjectName(current.getObjectName());
        archived.setFileHash(current.getFileHash());
        archived.setFileSize(current.getFileSize());
        archived.setFileName(current.getFileName());
        archived.setContentType(current.getContentType());
        archived.setStorageProvider(current.getStorageProvider());
        archived.setCreateBy(SecurityContextUtils.getUsername());
        archived.setCreateByUserId(SecurityContextUtils.getUserId());
        archived.setCreateTime(LocalDateTime.now());
        archived.setRemark(remark);
        versionMapper.insert(archived);
    }

    private int nextVersionNo(Long fileId) {
        Integer max = versionMapper.selectMaxVersionNo(fileId);
        return (max == null ? 0 : max) + 1;
    }

    private void tryDeleteObject(String objectName, Long excludeFileId, Long excludeVersionId) {
        if (!StringUtils.hasText(objectName)) {
            return;
        }
        long inFiles = sysFileMapper.countByObjectName(objectName, excludeFileId);
        long inVersions = versionMapper.countByObjectName(objectName, excludeVersionId);
        if (inFiles + inVersions > 0) {
            return;
        }
        try {
            fileStorageService.deleteObject(objectName);
        } catch (Exception e) {
            log.warn("清理版本 OSS 对象失败 objectName={}: {}", objectName, e.getMessage());
        }
    }

    private SysFile requireAccessibleActive(Long fileId, DataScope dataScope) {
        SysFile file = sysFileMapper.selectById(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件不存在或已删除");
        }
        if (!FileDataScopeSupport.isAccessible(file, dataScope)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权访问该文件");
        }
        return file;
    }

    private void validateFileSize(long size, FileMediaType mediaType) {
        Long maxSize = fileCenterProperties.getMaxSizeByMediaType()
                .getOrDefault(mediaType.getCode(), mediaType.getDefaultMaxSize());
        if (size > maxSize) {
            throw new BizException(ErrorCode.PARAM_INVALID,
                    String.format("文件大小超过限制（最大 %d MB）", maxSize / 1024 / 1024));
        }
    }

    private String resolveSuffix(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }

    private SysFileVersionVo toVo(SysFileVersion v, boolean current) {
        SysFileVersionVo vo = new SysFileVersionVo();
        BeanUtils.copyProperties(v, vo);
        vo.setCurrent(current);
        return vo;
    }
}
