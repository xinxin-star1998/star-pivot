package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileCategory;
import com.star.pivot.file.constant.FileMediaType;
import com.star.pivot.file.domain.dto.SysFileQueryDTO;
import com.star.pivot.file.domain.dto.SysFileRecycleQueryDTO;
import com.star.pivot.file.domain.dto.SysFileUploadDTO;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.entity.SysFileFolder;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.mapper.SysFileFolderMapper;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.service.ISysFileService;
import com.star.pivot.file.support.FileCenterUploadHelper;
import com.star.pivot.file.support.FileMediaTypeResolver;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.UploadResult;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    private final SysFileMapper sysFileMapper;
    private final SysFileFolderMapper sysFileFolderMapper;
    private final FileCenterUploadHelper fileCenterUploadHelper;
    private final FileMediaTypeResolver fileMediaTypeResolver;
    private final FileStorageService fileStorageService;
    private final FileCenterProperties fileCenterProperties;

    @Value("${oss.enabled:true}")
    private boolean ossEnabled;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo upload(MultipartFile file, Long folderId, SysFileUploadDTO uploadDTO) {
        AssertUtils.notNull(folderId, ErrorCode.PARAM_INVALID, "文件夹ID不能为空");
        SysFileFolder folder = sysFileFolderMapper.selectById(folderId);
        AssertUtils.notNull(folder, ErrorCode.NOT_FOUND, "文件夹不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(folder.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件夹不存在");
        }

        FileCategory category = FileCategory.of(folder.getCategory());
        FileMediaType mediaType = fileMediaTypeResolver.resolve(file);
        validateFileSize(file, mediaType);

        UploadResult uploadResult;
        try {
            uploadResult = fileCenterUploadHelper.upload(
                    file, category.getObjectPathSegment(), folderId);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        }

        SysFile entity = new SysFile();
        entity.setFolderId(folderId);
        entity.setCategory(category.getCode());
        entity.setMediaType(mediaType.getCode());
        entity.setFileName(file.getOriginalFilename());
        entity.setFileExt(fileMediaTypeResolver.extractExtension(file.getOriginalFilename()));
        entity.setContentType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setObjectName(uploadResult.getObjectName());
        entity.setStorageProvider(ossEnabled ? "OSS" : "LOCAL");
        entity.setDelFlag(FileBizConstants.DEL_FLAG_NORMAL);
        entity.setCreateBy(SecurityContextUtils.getUsername());
        entity.setCreateTime(LocalDateTime.now());

        if (uploadDTO != null) {
            entity.setBizType(uploadDTO.getBizType());
            entity.setBizId(uploadDTO.getBizId());
            entity.setRemark(uploadDTO.getRemark());
        }

        save(entity);
        return toVo(entity, uploadResult.getPresignedUrl());
    }

    @Override
    public PageResponse<SysFileVo> pageList(SysFileQueryDTO queryDTO) {
        Page<SysFile> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysFile> result = sysFileMapper.selectPageList(page, queryDTO);
        return toPageResponse(result, true);
    }

    @Override
    public SysFileVo getDetail(Long fileId) {
        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        return toVo(file, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDelete(List<Long> ids) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "删除ID不能为空");
        String username = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        for (Long id : ids) {
            SysFile file = getActiveFile(id);
            if (file == null) {
                continue;
            }
            update(new LambdaUpdateWrapper<SysFile>()
                    .eq(SysFile::getFileId, id)
                    .set(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_RECYCLE)
                    .set(SysFile::getDeleteBy, username)
                    .set(SysFile::getDeleteTime, now));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(List<Long> ids) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "恢复ID不能为空");
        sysFileMapper.restoreByIds(ids, SecurityContextUtils.getUsername());
    }

    @Override
    public PageResponse<SysFileVo> recyclePage(SysFileRecycleQueryDTO queryDTO) {
        Page<SysFile> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysFile> result = sysFileMapper.selectRecyclePageList(page, queryDTO);
        return toPageResponse(result, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveToFolder(List<Long> ids, Long targetFolderId) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "文件ID不能为空");
        AssertUtils.notNull(targetFolderId, ErrorCode.PARAM_INVALID, "目标文件夹不能为空");

        SysFileFolder targetFolder = sysFileFolderMapper.selectById(targetFolderId);
        AssertUtils.notNull(targetFolder, ErrorCode.NOT_FOUND, "目标文件夹不存在");

        String category = targetFolder.getCategory();
        String username = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        int moved = 0;

        for (Long id : ids) {
            SysFile file = getActiveFile(id);
            if (file == null) {
                continue;
            }
            if (!FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "回收站中的文件不可迁移");
            }
            if (targetFolderId.equals(file.getFolderId())) {
                continue;
            }
            update(new LambdaUpdateWrapper<SysFile>()
                    .eq(SysFile::getFileId, id)
                    .set(SysFile::getFolderId, targetFolderId)
                    .set(SysFile::getCategory, category)
                    .set(SysFile::getUpdateBy, username)
                    .set(SysFile::getUpdateTime, now));
            moved++;
        }

        if (moved == 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "没有可迁移的文件（可能已在目标文件夹）");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long fileId, String fileName) {
        AssertUtils.notNull(fileId, ErrorCode.PARAM_INVALID, "文件ID不能为空");
        if (!StringUtils.hasText(fileName)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件名不能为空");
        }

        String trimmed = fileName.trim();
        validateDisplayFileName(trimmed);

        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");

        String finalName = normalizeDisplayFileName(trimmed, file.getFileExt());
        if (finalName.equals(file.getFileName())) {
            return;
        }

        long duplicate = count(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFolderId, file.getFolderId())
                .eq(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL)
                .eq(SysFile::getFileName, finalName)
                .ne(SysFile::getFileId, fileId));
        if (duplicate > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "当前文件夹下已存在同名文件");
        }

        String newExt = fileMediaTypeResolver.extractExtension(finalName);
        update(new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .set(SysFile::getFileName, finalName)
                .set(SysFile::getFileExt, StringUtils.hasText(newExt) ? newExt : file.getFileExt())
                .set(SysFile::getUpdateBy, SecurityContextUtils.getUsername())
                .set(SysFile::getUpdateTime, LocalDateTime.now()));
    }

    @Override
    public Map<String, String> previewUrl(Long fileId) {
        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "已删除文件不可预览");
        }
        try {
            String url = fileStorageService.getPresignedUrl(file.getObjectName());
            return Map.of("url", url, "objectName", file.getObjectName());
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "获取预览链接失败");
        }
    }

    private void validateFileSize(MultipartFile file, FileMediaType mediaType) {
        Long maxSize = fileCenterProperties.getMaxSizeByMediaType()
                .getOrDefault(mediaType.getCode(), mediaType.getDefaultMaxSize());
        if (file.getSize() > maxSize) {
            throw new BizException(ErrorCode.PARAM_INVALID,
                    String.format("文件大小超过限制（最大 %d MB）", maxSize / 1024 / 1024));
        }
    }

    private void validateDisplayFileName(String fileName) {
        if (fileName.length() > 255) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件名长度不能超过255");
        }
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件名不能包含路径分隔符");
        }
    }

    private String normalizeDisplayFileName(String input, String originalExt) {
        if (fileMediaTypeResolver.extractExtension(input).length() > 0) {
            return input;
        }
        if (StringUtils.hasText(originalExt)) {
            return input + "." + originalExt;
        }
        return input;
    }

    private PageResponse<SysFileVo> toPageResponse(IPage<SysFile> page, boolean includeDisplayUrl) {
        List<SysFile> records = page.getRecords();
        Map<Long, String> folderNameMap = loadFolderNameMap(records);
        Map<String, String> displayUrlMap = includeDisplayUrl ? batchResolveDisplayUrls(records) : Collections.emptyMap();

        PageResponse<SysFileVo> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setPageNum(page.getCurrent());
        response.setPageSize(page.getSize());
        response.setPageCount(page.getPages());
        response.setRows(records.stream()
                .map(file -> buildVo(file, null, folderNameMap, displayUrlMap, includeDisplayUrl))
                .collect(Collectors.toList()));
        return response;
    }

    private Map<Long, String> loadFolderNameMap(List<SysFile> files) {
        Set<Long> folderIds = files.stream()
                .map(SysFile::getFolderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (folderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return sysFileFolderMapper.selectBatchIds(folderIds).stream()
                .collect(Collectors.toMap(
                        SysFileFolder::getFolderId,
                        SysFileFolder::getFolderName,
                        (left, right) -> left));
    }

    private Map<String, String> batchResolveDisplayUrls(List<SysFile> files) {
        List<String> objectNames = files.stream()
                .filter(file -> FileMediaType.IMAGE.getCode().equals(file.getMediaType()))
                .map(SysFile::getObjectName)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        if (objectNames.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return fileStorageService.getPresignedUrls(objectNames);
        } catch (Exception e) {
            return objectNames.stream()
                    .collect(Collectors.toMap(
                            objectName -> objectName,
                            fileStorageService::getPermanentUrl,
                            (left, right) -> left));
        }
    }

    private SysFileVo toVo(SysFile file, String presignedUrl) {
        Map<Long, String> folderNameMap = Collections.emptyMap();
        if (file.getFolderId() != null) {
            SysFileFolder folder = sysFileFolderMapper.selectById(file.getFolderId());
            if (folder != null) {
                folderNameMap = Map.of(folder.getFolderId(), folder.getFolderName());
            }
        }
        return buildVo(file, presignedUrl, folderNameMap, Collections.emptyMap(), true);
    }

    private SysFileVo buildVo(
            SysFile file,
            String presignedUrl,
            Map<Long, String> folderNameMap,
            Map<String, String> displayUrlMap,
            boolean includeDisplayUrl) {
        SysFileVo vo = new SysFileVo();
        BeanUtils.copyProperties(file, vo);

        try {
            FileCategory category = FileCategory.of(file.getCategory());
            vo.setCategoryLabel(category.getLabel());
        } catch (IllegalArgumentException ignored) {
            vo.setCategoryLabel(file.getCategory());
        }

        FileMediaType mediaType = FileMediaType.of(file.getMediaType());
        vo.setMediaTypeLabel(mediaType.getLabel());
        vo.setPreviewMode(resolvePreviewMode(mediaType, file.getFileExt()));

        if (file.getFolderId() != null) {
            vo.setFolderName(folderNameMap.get(file.getFolderId()));
        }

        if (!includeDisplayUrl) {
            return vo;
        }

        if (StringUtils.hasText(presignedUrl)) {
            vo.setDisplayUrl(presignedUrl);
            return vo;
        }

        if (!StringUtils.hasText(file.getObjectName())) {
            return vo;
        }

        if (FileMediaType.IMAGE.getCode().equals(file.getMediaType())) {
            String cachedUrl = displayUrlMap.get(file.getObjectName());
            if (StringUtils.hasText(cachedUrl)) {
                vo.setDisplayUrl(cachedUrl);
                return vo;
            }
            try {
                vo.setDisplayUrl(fileStorageService.getPresignedUrl(file.getObjectName()));
            } catch (Exception e) {
                vo.setDisplayUrl(fileStorageService.getPermanentUrl(file.getObjectName()));
            }
            return vo;
        }

        if (!displayUrlMap.isEmpty()) {
            return vo;
        }

        try {
            vo.setDisplayUrl(fileStorageService.getPresignedUrl(file.getObjectName()));
        } catch (Exception e) {
            vo.setDisplayUrl(fileStorageService.getPermanentUrl(file.getObjectName()));
        }
        return vo;
    }

    private String resolvePreviewMode(FileMediaType mediaType, String fileExt) {
        return switch (mediaType) {
            case IMAGE -> "image";
            case VIDEO -> "video";
            case AUDIO -> "audio";
            case DOCUMENT -> "pdf".equalsIgnoreCase(fileExt) ? "pdf" : "download";
            default -> "download";
        };
    }

    private SysFile getActiveFile(Long fileId) {
        if (fileId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .eq(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
    }
}
