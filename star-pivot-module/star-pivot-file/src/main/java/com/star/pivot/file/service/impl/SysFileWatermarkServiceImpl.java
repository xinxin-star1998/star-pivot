package com.star.pivot.file.service.impl;

import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileMediaType;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.vo.SysFileWatermarkVo;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.file.service.ISysFileMetaService;
import com.star.pivot.file.service.ISysFileWatermarkService;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.file.support.ImageWatermarkHelper;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.Color;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileWatermarkServiceImpl implements ISysFileWatermarkService {

    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final FileCenterProperties fileCenterProperties;
    private final SysFileMapper sysFileMapper;
    private final FileStorageService fileStorageService;
    private final ISysFileAuditService sysFileAuditService;
    private final ISysFileMetaService sysFileMetaService;

    @Override
    public SysFileWatermarkVo currentConfig() {
        String username = SecurityContextUtils.getUsername();
        if (!StringUtils.hasText(username)) {
            username = guestName();
        }
        return resolveConfig(username);
    }

    @Override
    public SysFileWatermarkVo resolveConfig(String displayName) {
        FileCenterProperties.Watermark cfg = watermarkCfg();
        String name = StringUtils.hasText(displayName) ? displayName.trim() : guestName();
        return SysFileWatermarkVo.builder()
                .enabled(cfg.isEnabled())
                .content(cfg.isEnabled() ? renderContent(cfg.getContentTemplate(), name) : "")
                .fontSize(cfg.getFontSize())
                .fontColor(cfg.getFontColor())
                .rotate(cfg.getRotate())
                .gapX(cfg.getGapX())
                .gapY(cfg.getGapY())
                .downloadEnabled(cfg.isEnabled() && cfg.isDownloadEnabled())
                .build();
    }

    @Override
    public void downloadWatermarked(Long fileId, DataScope dataScope, HttpServletResponse response) {
        SysFile file = sysFileMapper.selectById(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件不存在或已删除");
        }
        if (!FileDataScopeSupport.isAccessible(file, dataScope)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权访问该文件");
        }
        AssertUtils.isTrue(StringUtils.hasText(file.getObjectName()), ErrorCode.PARAM_INVALID, "文件对象不存在");

        byte[] bytes;
        try {
            bytes = fileStorageService.downloadObject(file.getObjectName());
        } catch (Exception e) {
            log.warn("下载原文件失败: fileId={}, err={}", fileId, e.getMessage());
            throw new BizException(ErrorCode.INTERNAL_ERROR, "下载文件失败");
        }

        FileCenterProperties.Watermark cfg = watermarkCfg();
        boolean isImage = FileMediaType.IMAGE.equals(FileMediaType.of(file.getMediaType()));
        if (cfg.isEnabled() && cfg.isDownloadEnabled() && isImage
                && ImageWatermarkHelper.isSupportedFormat(file.getFileExt(), file.getContentType())) {
            try {
                String text = renderContent(cfg.getContentTemplate(),
                        StringUtils.hasText(SecurityContextUtils.getUsername())
                                ? SecurityContextUtils.getUsername()
                                : guestName());
                Color color = ImageWatermarkHelper.parseColor(cfg.getFontColor(), 0.12f);
                bytes = ImageWatermarkHelper.apply(
                        bytes,
                        file.getFileExt(),
                        file.getContentType(),
                        text,
                        cfg.getFontSize(),
                        color,
                        cfg.getRotate(),
                        cfg.getGapX(),
                        cfg.getGapY());
            } catch (Exception e) {
                log.warn("叠加水印失败，回退原图: fileId={}, err={}", fileId, e.getMessage());
            }
        }

        String fileName = StringUtils.hasText(file.getFileName()) ? file.getFileName() : "file";
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";
        response.reset();
        response.setContentType(contentType);
        response.setContentLength(bytes.length);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodeAsciiFileName(fileName) + "\"; filename*=UTF-8''"
                        + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        try {
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "写出文件失败");
        }

        try {
            sysFileMetaService.touchRecent(fileId, dataScope);
        } catch (Exception ignored) {
            // ignore
        }
        sysFileAuditService.record(FileAuditAction.DOWNLOAD, fileId, file.getFileName(),
                cfg.isEnabled() && cfg.isDownloadEnabled() && isImage ? "水印下载" : "下载");
    }

    private String renderContent(String template, String displayName) {
        LocalDateTime now = LocalDateTime.now();
        String tpl = StringUtils.hasText(template) ? template : "{username} {datetime}";
        String name = StringUtils.hasText(displayName) ? displayName : guestName();
        return tpl
                .replace("{username}", name)
                .replace("{nickname}", name)
                .replace("{datetime}", now.format(DATETIME))
                .replace("{date}", now.format(DATE))
                .replace("{time}", now.format(TIME))
                .trim();
    }

    private FileCenterProperties.Watermark watermarkCfg() {
        FileCenterProperties.Watermark cfg = fileCenterProperties.getWatermark();
        return cfg != null ? cfg : new FileCenterProperties.Watermark();
    }

    private String guestName() {
        FileCenterProperties.Watermark cfg = watermarkCfg();
        return StringUtils.hasText(cfg.getGuestName()) ? cfg.getGuestName() : "分享访客";
    }

    private static String encodeAsciiFileName(String fileName) {
        StringBuilder sb = new StringBuilder();
        for (char c : fileName.toCharArray()) {
            if (c >= 32 && c < 127 && c != '"' && c != '\\') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.length() > 0 ? sb.toString() : "file";
    }
}
