package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileCategory;
import com.star.pivot.file.constant.FileMediaType;
import com.star.pivot.file.domain.dto.*;
import com.star.pivot.file.domain.entity.SysFile;
import com.star.pivot.file.domain.entity.SysFileFolder;
import com.star.pivot.file.domain.vo.SysFileHashCheckVo;
import com.star.pivot.file.domain.vo.SysFileMultipartInitVo;
import com.star.pivot.file.domain.vo.SysFileUsageStatVo;
import com.star.pivot.file.domain.vo.SysFileUsageSummaryVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.mapper.SysFileFolderMapper;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.file.service.ISysFileMetaService;
import com.star.pivot.file.service.ISysFileService;
import com.star.pivot.file.service.ISysFileVersionService;
import com.star.pivot.file.support.FileCenterUploadHelper;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.file.support.FileHashUtils;
import com.star.pivot.file.support.FileMediaTypeResolver;
import com.star.pivot.file.support.MultipartUploadSessionStore;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.UploadResult;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    private final SysFileMapper sysFileMapper;
    private final SysFileFolderMapper sysFileFolderMapper;
    private final FileCenterUploadHelper fileCenterUploadHelper;
    private final FileMediaTypeResolver fileMediaTypeResolver;
    private final FileStorageService fileStorageService;
    private final FileCenterProperties fileCenterProperties;
    private final MultipartUploadSessionStore multipartUploadSessionStore;
    private final ISysFileMetaService sysFileMetaService;
    private final ISysFileVersionService sysFileVersionService;
    private final ISysFileAuditService sysFileAuditService;

    @Value("${oss.enabled:true}")
    private boolean ossEnabled;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo upload(MultipartFile file, Long folderId, SysFileUploadDTO uploadDTO) {
        AssertUtils.notNull(folderId, ErrorCode.PARAM_INVALID, "文件夹ID不能为空");
        SysFileFolder folder = requireActiveFolder(folderId);

        FileCategory category = FileCategory.of(folder.getCategory());
        FileMediaType mediaType = fileMediaTypeResolver.resolve(file);
        validateFileSize(file.getSize(), mediaType);

        String fileHash = uploadDTO != null ? uploadDTO.getFileHash() : null;
        byte[] bytes = null;
        if (!StringUtils.hasText(fileHash)) {
            try {
                bytes = file.getBytes();
                fileHash = FileHashUtils.sha256Hex(bytes);
            } catch (Exception e) {
                throw new BizException(ErrorCode.INTERNAL_ERROR, "读取文件失败: " + e.getMessage());
            }
        }

        SysFile existing = sysFileMapper.selectByHashAndSize(fileHash, file.getSize());
        if (existing != null && StringUtils.hasText(existing.getObjectName())) {
            return saveMetadataClone(existing, folder, file.getOriginalFilename(), mediaType,
                    file.getContentType(), fileHash, uploadDTO);
        }

        UploadResult uploadResult;
        try {
            if (bytes != null) {
                String suffix = resolveSuffix(file.getOriginalFilename());
                String objectName = fileCenterUploadHelper.buildObjectName(
                        category.getObjectPathSegment(), folderId, suffix);
                fileStorageService.uploadFileInternal(
                        new InMemoryMultipartFile(file.getOriginalFilename(), file.getContentType(), bytes),
                        objectName);
                uploadResult = UploadResult.builder()
                        .objectName(objectName)
                        .permanentUrl(fileStorageService.getPermanentUrl(objectName))
                        .presignedUrl(fileStorageService.getPresignedUrl(objectName))
                        .build();
            } else {
                uploadResult = fileCenterUploadHelper.upload(file, category.getObjectPathSegment(), folderId);
            }
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        }

        SysFile entity = buildNewEntity(folder, category, mediaType, file.getOriginalFilename(),
                file.getContentType(), file.getSize(), uploadResult.getObjectName(), fileHash, uploadDTO);
        save(entity);
        sysFileAuditService.record(FileAuditAction.UPLOAD, entity.getFileId(), entity.getFileName(), "上传文件");
        return toVo(entity, uploadResult.getPresignedUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileHashCheckVo checkHashAndInstantUpload(SysFileHashCheckDTO dto, SysFileUploadDTO owner) {
        SysFileFolder folder = requireActiveFolder(dto.getFolderId());
        SysFile existing = sysFileMapper.selectByHashAndSize(dto.getFileHash(), dto.getFileSize());
        if (existing == null || !StringUtils.hasText(existing.getObjectName())) {
            return SysFileHashCheckVo.builder().instant(false).build();
        }

        FileMediaType mediaType = FileMediaType.of(existing.getMediaType());
        String fileName = StringUtils.hasText(dto.getFileName()) ? dto.getFileName() : existing.getFileName();
        SysFileUploadDTO uploadDTO = owner != null ? owner : new SysFileUploadDTO();
        uploadDTO.setBizType(dto.getBizType());
        uploadDTO.setBizId(dto.getBizId());
        uploadDTO.setRemark(dto.getRemark());
        uploadDTO.setFileHash(dto.getFileHash());

        SysFileVo vo = saveMetadataClone(existing, folder, fileName, mediaType,
                existing.getContentType(), dto.getFileHash(), uploadDTO);
        return SysFileHashCheckVo.builder().instant(true).file(vo).build();
    }

    @Override
    public SysFileMultipartInitVo initMultipart(SysFileMultipartInitDTO dto, SysFileUploadDTO owner) {
        SysFileFolder folder = requireActiveFolder(dto.getFolderId());
        FileCategory category = FileCategory.of(folder.getCategory());
        FileMediaType mediaType = fileMediaTypeResolver.resolveByFilename(dto.getFileName(), dto.getContentType());
        validateFileSize(dto.getFileSize(), mediaType);

        if (StringUtils.hasText(dto.getFileHash())) {
            SysFile existing = sysFileMapper.selectByHashAndSize(dto.getFileHash(), dto.getFileSize());
            if (existing != null) {
                throw new BizException(ErrorCode.PARAM_INVALID, "文件可秒传，请先调用 check-hash");
            }
        }

        String suffix = resolveSuffix(dto.getFileName());
        String objectName = fileCenterUploadHelper.buildObjectName(
                category.getObjectPathSegment(), dto.getFolderId(), suffix);
        String uploadId;
        try {
            uploadId = fileStorageService.initiateMultipartUpload(objectName, dto.getContentType());
        } catch (UnsupportedOperationException e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "当前存储不支持分片上传");
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "初始化分片上传失败: " + e.getMessage());
        }

        long partSize = Math.max(100L * 1024, fileCenterProperties.getMultipartPartSize());
        MultipartUploadSessionStore.Session session = new MultipartUploadSessionStore.Session();
        session.setUploadId(uploadId);
        session.setObjectName(objectName);
        session.setFolderId(dto.getFolderId());
        session.setFileName(dto.getFileName());
        session.setFileSize(dto.getFileSize());
        session.setContentType(dto.getContentType());
        session.setFileHash(dto.getFileHash());
        session.setMediaType(mediaType.getCode());
        session.setBizType(dto.getBizType());
        session.setBizId(dto.getBizId());
        session.setRemark(dto.getRemark());
        session.setPartSize(partSize);
        if (owner != null) {
            session.setCreateByUserId(owner.getCreateByUserId());
            session.setCreateDeptId(owner.getCreateDeptId());
        }
        multipartUploadSessionStore.save(session);

        List<Integer> uploaded = Collections.emptyList();
        List<SysFileMultipartInitVo.PartItem> uploadedDetails = Collections.emptyList();
        try {
            List<Map.Entry<Integer, String>> listed =
                    fileStorageService.listUploadedParts(objectName, uploadId);
            uploaded = listed.stream().map(Map.Entry::getKey).collect(Collectors.toList());
            uploadedDetails = listed.stream()
                    .map(e -> SysFileMultipartInitVo.PartItem.builder()
                            .partNumber(e.getKey())
                            .etag(e.getValue())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            // 新上传无已传分片
        }

        return SysFileMultipartInitVo.builder()
                .uploadId(uploadId)
                .objectName(objectName)
                .partSize(partSize)
                .uploadedParts(uploaded)
                .uploadedPartDetails(uploadedDetails)
                .build();
    }

    @Override
    public Map<String, String> uploadMultipartPart(String uploadId, String objectName,
                                                   int partNumber, MultipartFile chunk) {
        AssertUtils.notEmpty(uploadId, ErrorCode.PARAM_INVALID, "uploadId不能为空");
        AssertUtils.notEmpty(objectName, ErrorCode.PARAM_INVALID, "objectName不能为空");
        if (partNumber < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "分片号无效");
        }
        try {
            String etag = fileStorageService.uploadPart(
                    objectName, uploadId, partNumber, chunk.getInputStream(), chunk.getSize());
            return Map.of("etag", etag, "partNumber", String.valueOf(partNumber));
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "分片上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo completeMultipart(SysFileMultipartCompleteDTO dto, SysFileUploadDTO owner) {
        SysFileFolder folder = requireActiveFolder(dto.getFolderId());
        FileCategory category = FileCategory.of(folder.getCategory());

        List<Map.Entry<Integer, String>> partETags = dto.getParts().stream()
                .sorted(Comparator.comparingInt(SysFileMultipartCompleteDTO.PartETagItem::getPartNumber))
                .map(p -> Map.entry(p.getPartNumber(), p.getEtag()))
                .collect(Collectors.toList());
        try {
            fileStorageService.completeMultipartUpload(dto.getObjectName(), dto.getUploadId(), partETags);
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "完成分片上传失败: " + e.getMessage());
        }

        MultipartUploadSessionStore.Session session = multipartUploadSessionStore.get(dto.getUploadId());
        String mediaTypeCode = StringUtils.hasText(dto.getMediaType())
                ? dto.getMediaType()
                : (session != null ? session.getMediaType() : null);
        FileMediaType mediaType = StringUtils.hasText(mediaTypeCode)
                ? FileMediaType.of(mediaTypeCode)
                : fileMediaTypeResolver.resolveByFilename(dto.getFileName(), dto.getContentType());

        String fileHash = dto.getFileHash();
        if (!StringUtils.hasText(fileHash) && session != null) {
            fileHash = session.getFileHash();
        }

        SysFileUploadDTO uploadDTO = owner != null ? owner : new SysFileUploadDTO();
        if (session != null) {
            if (uploadDTO.getCreateByUserId() == null) {
                uploadDTO.setCreateByUserId(session.getCreateByUserId());
            }
            if (uploadDTO.getCreateDeptId() == null) {
                uploadDTO.setCreateDeptId(session.getCreateDeptId());
            }
            if (!StringUtils.hasText(uploadDTO.getBizType())) {
                uploadDTO.setBizType(session.getBizType());
            }
            if (!StringUtils.hasText(uploadDTO.getBizId())) {
                uploadDTO.setBizId(session.getBizId());
            }
            if (!StringUtils.hasText(uploadDTO.getRemark())) {
                uploadDTO.setRemark(session.getRemark());
            }
        }
        if (StringUtils.hasText(dto.getBizType())) {
            uploadDTO.setBizType(dto.getBizType());
        }
        if (StringUtils.hasText(dto.getBizId())) {
            uploadDTO.setBizId(dto.getBizId());
        }
        if (StringUtils.hasText(dto.getRemark())) {
            uploadDTO.setRemark(dto.getRemark());
        }
        uploadDTO.setFileHash(fileHash);

        SysFile entity = buildNewEntity(folder, category, mediaType, dto.getFileName(),
                dto.getContentType(), dto.getFileSize(), dto.getObjectName(), fileHash, uploadDTO);
        save(entity);
        multipartUploadSessionStore.remove(dto.getUploadId());
        return toVo(entity, null);
    }

    @Override
    public void abortMultipart(String uploadId, String objectName) {
        try {
            fileStorageService.abortMultipartUpload(objectName, uploadId);
        } catch (Exception e) {
            log.warn("取消分片上传失败 uploadId={}: {}", uploadId, e.getMessage());
        }
        multipartUploadSessionStore.remove(uploadId);
    }

    @Override
    public SysFileMultipartInitVo multipartStatus(String uploadId, String objectName) {
        MultipartUploadSessionStore.Session session = multipartUploadSessionStore.get(uploadId);
        long partSize = session != null ? session.getPartSize() : fileCenterProperties.getMultipartPartSize();
        List<SysFileMultipartInitVo.PartItem> details = Collections.emptyList();
        try {
            details = fileStorageService.listUploadedParts(objectName, uploadId).stream()
                    .map(e -> SysFileMultipartInitVo.PartItem.builder()
                            .partNumber(e.getKey())
                            .etag(e.getValue())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "查询分片状态失败，请重新上传");
        }
        return SysFileMultipartInitVo.builder()
                .uploadId(uploadId)
                .objectName(objectName)
                .partSize(Math.max(100L * 1024, partSize))
                .uploadedParts(details.stream().map(SysFileMultipartInitVo.PartItem::getPartNumber).collect(Collectors.toList()))
                .uploadedPartDetails(details)
                .build();
    }

    @Override
    public PageResponse<SysFileVo> pageList(SysFileQueryDTO queryDTO) {
        if (queryDTO.getCurrentUserId() == null) {
            queryDTO.setCurrentUserId(SecurityContextUtils.getUserId());
        }
        Page<SysFile> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysFile> result = sysFileMapper.selectPageList(page, queryDTO);
        PageResponse<SysFileVo> response = toPageResponse(result, true);
        sysFileMetaService.enrichList(response.getRows());
        return response;
    }

    @Override
    public SysFileVo getDetail(Long fileId, DataScope dataScope) {
        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        assertAccessible(file, dataScope);
        return toVo(file, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDelete(List<Long> ids, DataScope dataScope) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "删除ID不能为空");
        String username = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        for (Long id : ids) {
            SysFile file = getActiveFile(id);
            if (file == null || !FileDataScopeSupport.isAccessible(file, dataScope)) {
                continue;
            }
            update(new LambdaUpdateWrapper<SysFile>()
                    .eq(SysFile::getFileId, id)
                    .set(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_RECYCLE)
                    .set(SysFile::getDeleteBy, username)
                    .set(SysFile::getDeleteTime, now));
            sysFileAuditService.record(FileAuditAction.DELETE, id, file.getFileName(), "移入回收站");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(List<Long> ids, DataScope dataScope) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "恢复ID不能为空");
        List<SysFile> files = sysFileMapper.selectRecycleByIds(ids);
        List<Long> allowed = files.stream()
                .filter(f -> FileDataScopeSupport.isAccessible(f, dataScope))
                .map(SysFile::getFileId)
                .collect(Collectors.toList());
        if (allowed.isEmpty()) {
            throw new BizException(ErrorCode.FORBIDDEN, "没有可恢复的文件");
        }
        sysFileMapper.restoreByIds(allowed, SecurityContextUtils.getUsername());
        for (SysFile f : files) {
            if (allowed.contains(f.getFileId())) {
                sysFileAuditService.record(FileAuditAction.RESTORE, f.getFileId(), f.getFileName(), "从回收站恢复");
            }
        }
    }

    @Override
    public PageResponse<SysFileVo> recyclePage(SysFileRecycleQueryDTO queryDTO) {
        Page<SysFile> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysFile> result = sysFileMapper.selectRecyclePageList(page, queryDTO);
        // 回收站同样返回图片预览地址，避免切回全部文件时表格/缩略图状态异常
        return toPageResponse(result, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purge(List<Long> ids, DataScope dataScope) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "删除ID不能为空");
        List<SysFile> files = sysFileMapper.selectRecycleByIds(ids).stream()
                .filter(f -> FileDataScopeSupport.isAccessible(f, dataScope))
                .collect(Collectors.toList());
        if (files.isEmpty()) {
            throw new BizException(ErrorCode.NOT_FOUND, "没有可彻底删除的文件");
        }
        for (SysFile f : files) {
            sysFileAuditService.record(FileAuditAction.PURGE, f.getFileId(), f.getFileName(), "彻底删除");
        }
        physicalPurge(files);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int clearRecycle(DataScope dataScope) {
        SysFileRecycleQueryDTO query = new SysFileRecycleQueryDTO();
        FileDataScopeSupport.applyToQuery(dataScope,
                query::setDataScope, query::setDeptIds, query::setUserId, query::setUserDeptId);

        int total = 0;
        while (true) {
            List<Long> batchIds = sysFileMapper.selectAllRecycleIds(query, 200);
            if (batchIds == null || batchIds.isEmpty()) {
                break;
            }
            List<SysFile> files = sysFileMapper.selectRecycleByIds(batchIds);
            physicalPurge(files);
            total += files.size();
            if (batchIds.size() < 200) {
                break;
            }
        }
        return total;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveToFolder(List<Long> ids, Long targetFolderId, DataScope dataScope) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "文件ID不能为空");
        AssertUtils.notNull(targetFolderId, ErrorCode.PARAM_INVALID, "目标文件夹不能为空");

        SysFileFolder targetFolder = requireActiveFolder(targetFolderId);
        String category = targetFolder.getCategory();
        String username = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        int moved = 0;

        for (Long id : ids) {
            SysFile file = getActiveFile(id);
            if (file == null || !FileDataScopeSupport.isAccessible(file, dataScope)) {
                continue;
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
            sysFileAuditService.record(FileAuditAction.MOVE, id, file.getFileName(),
                    "迁移到文件夹 " + targetFolderId);
            moved++;
        }

        if (moved == 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "没有可迁移的文件（可能已在目标文件夹或无权限）");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long fileId, String fileName, DataScope dataScope) {
        AssertUtils.notNull(fileId, ErrorCode.PARAM_INVALID, "文件ID不能为空");
        if (!StringUtils.hasText(fileName)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件名不能为空");
        }

        String trimmed = fileName.trim();
        validateDisplayFileName(trimmed);

        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        assertAccessible(file, dataScope);

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

        String oldName = file.getFileName();
        String newExt = fileMediaTypeResolver.extractExtension(finalName);
        update(new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .set(SysFile::getFileName, finalName)
                .set(SysFile::getFileExt, StringUtils.hasText(newExt) ? newExt : file.getFileExt())
                .set(SysFile::getUpdateBy, SecurityContextUtils.getUsername())
                .set(SysFile::getUpdateTime, LocalDateTime.now()));
        sysFileAuditService.record(FileAuditAction.RENAME, fileId, finalName,
                "重命名: " + oldName + " -> " + finalName);
    }

    @Override
    public Map<String, String> previewUrl(Long fileId, DataScope dataScope) {
        SysFile file = getActiveFile(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        assertAccessible(file, dataScope);
        try {
            sysFileMetaService.touchRecent(fileId, dataScope);
        } catch (Exception e) {
            log.debug("记录最近访问失败: fileId={}", fileId, e);
        }
        try {
            String url = fileStorageService.getPresignedUrl(file.getObjectName());
            Map<String, String> result = new LinkedHashMap<>();
            result.put("url", url);
            result.put("objectName", file.getObjectName());
            String mode = resolvePreviewMode(FileMediaType.of(file.getMediaType()), file.getFileExt());
            result.put("mode", mode);
            if ("office".equals(mode)) {
                String viewerUrl = buildOfficeViewerUrl(url);
                if (StringUtils.hasText(viewerUrl)) {
                    result.put("viewerUrl", viewerUrl);
                }
            }
            sysFileAuditService.record(FileAuditAction.DOWNLOAD, fileId, file.getFileName(), "预览/下载链接");
            return result;
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "获取预览链接失败");
        }
    }

    @Override
    public void downloadZip(List<Long> ids, DataScope dataScope, HttpServletResponse response) {
        AssertUtils.notEmpty(ids, ErrorCode.PARAM_INVALID, "请选择要下载的文件");
        FileCenterProperties.Download download = fileCenterProperties.getDownload();
        int maxFiles = download != null ? download.getZipMaxFiles() : 50;
        long maxBytes = download != null ? download.getZipMaxTotalBytes() : 500L * 1024 * 1024;
        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>(ids);
        if (uniqueIds.size() > maxFiles) {
            throw new BizException(ErrorCode.PARAM_INVALID, "单次最多下载 " + maxFiles + " 个文件");
        }

        List<SysFile> files = new ArrayList<>();
        long totalSize = 0;
        for (Long id : uniqueIds) {
            SysFile file = getActiveFile(id);
            if (file == null || !FileDataScopeSupport.isAccessible(file, dataScope)) {
                continue;
            }
            if (!StringUtils.hasText(file.getObjectName())) {
                continue;
            }
            long size = file.getFileSize() != null ? file.getFileSize() : 0L;
            totalSize += size;
            if (totalSize > maxBytes) {
                throw new BizException(ErrorCode.PARAM_INVALID,
                        "所选文件总大小超过限制（最大 " + (maxBytes / 1024 / 1024) + " MB）");
            }
            files.add(file);
        }
        if (files.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "没有可下载的文件");
        }

        String zipName = "files-" + System.currentTimeMillis() + ".zip";
        response.reset();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + zipName + "\"; filename*=UTF-8''"
                        + URLEncoder.encode(zipName, StandardCharsets.UTF_8));
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

        Set<String> usedNames = new HashSet<>();
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (SysFile file : files) {
                byte[] bytes;
                try {
                    bytes = fileStorageService.downloadObject(file.getObjectName());
                } catch (Exception e) {
                    log.warn("打包下载跳过文件: fileId={}, err={}", file.getFileId(), e.getMessage());
                    continue;
                }
                String entryName = uniqueZipEntryName(file.getFileName(), usedNames);
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(bytes);
                zos.closeEntry();
                try {
                    sysFileMetaService.touchRecent(file.getFileId(), dataScope);
                } catch (Exception ignored) {
                    // ignore
                }
                sysFileAuditService.record(FileAuditAction.ZIP_DOWNLOAD, file.getFileId(),
                        file.getFileName(), "打包下载");
            }
            zos.finish();
        } catch (IOException e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "打包下载失败");
        }
    }

    private String uniqueZipEntryName(String fileName, Set<String> usedNames) {
        String base = StringUtils.hasText(fileName) ? fileName : "file";
        base = base.replace("\\", "_").replace("/", "_");
        if (!usedNames.contains(base)) {
            usedNames.add(base);
            return base;
        }
        String name = base;
        String ext = "";
        int dot = base.lastIndexOf('.');
        if (dot > 0) {
            name = base.substring(0, dot);
            ext = base.substring(dot);
        }
        int i = 1;
        String candidate;
        do {
            candidate = name + "(" + i++ + ")" + ext;
        } while (usedNames.contains(candidate));
        usedNames.add(candidate);
        return candidate;
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
                String b64 = java.util.Base64.getEncoder()
                        .encodeToString(fileUrl.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                return base + "/onlinePreview?url=" + java.net.URLEncoder.encode(b64, java.nio.charset.StandardCharsets.UTF_8);
            }
            return "https://view.officeapps.live.com/op/embed.aspx?src=" + encoded;
        } catch (Exception e) {
            return null;
        }
    }

    private void physicalPurge(List<SysFile> files) {
        for (SysFile file : files) {
            String objectName = file.getObjectName();
            sysFileVersionService.purgeVersionsOfFile(file.getFileId(), objectName);
            long others = StringUtils.hasText(objectName)
                    ? sysFileMapper.countByObjectName(objectName, file.getFileId())
                    : 0;
            sysFileMapper.physicalDeleteByIds(List.of(file.getFileId()));
            if (others == 0 && StringUtils.hasText(objectName)) {
                try {
                    fileStorageService.deleteObject(objectName);
                } catch (Exception e) {
                    log.warn("清理 OSS 对象失败 objectName={}: {}", objectName, e.getMessage());
                }
            }
        }
    }

    @Override
    public SysFileUsageSummaryVo usageStats(SysFileUsageQueryDTO queryDTO) {
        SysFileUsageStatVo summary = sysFileMapper.selectUsageSummary(queryDTO);
        if (summary == null) {
            summary = new SysFileUsageStatVo();
            summary.setFileCount(0L);
            summary.setTotalBytes(0L);
            summary.setUniqueObjects(0L);
        }
        String groupBy = queryDTO.getGroupBy();
        List<SysFileUsageStatVo> items = "dept".equalsIgnoreCase(groupBy)
                ? sysFileMapper.selectUsageGroupByDept(queryDTO)
                : sysFileMapper.selectUsageGroupByUser(queryDTO);
        return SysFileUsageSummaryVo.builder()
                .fileCount(summary.getFileCount())
                .totalBytes(summary.getTotalBytes())
                .uniqueObjects(summary.getUniqueObjects())
                .items(items != null ? items : List.of())
                .build();
    }

    private SysFileVo saveMetadataClone(SysFile existing, SysFileFolder folder, String fileName,
                                        FileMediaType mediaType, String contentType, String fileHash,
                                        SysFileUploadDTO uploadDTO) {
        FileCategory category = FileCategory.of(folder.getCategory());
        SysFile entity = buildNewEntity(folder, category, mediaType, fileName, contentType,
                existing.getFileSize(), existing.getObjectName(), fileHash, uploadDTO);
        entity.setStorageProvider(existing.getStorageProvider());
        save(entity);
        sysFileAuditService.record(FileAuditAction.UPLOAD, entity.getFileId(), entity.getFileName(), "秒传/复用对象");
        return toVo(entity, null);
    }

    private SysFile buildNewEntity(SysFileFolder folder, FileCategory category, FileMediaType mediaType,
                                   String fileName, String contentType, Long fileSize, String objectName,
                                   String fileHash, SysFileUploadDTO uploadDTO) {
        SysFile entity = new SysFile();
        entity.setFolderId(folder.getFolderId());
        entity.setCategory(category.getCode());
        entity.setMediaType(mediaType.getCode());
        entity.setFileName(fileName);
        entity.setFileExt(fileMediaTypeResolver.extractExtension(fileName));
        entity.setContentType(contentType);
        entity.setFileSize(fileSize);
        entity.setObjectName(objectName);
        entity.setFileHash(fileHash);
        entity.setStorageProvider(ossEnabled ? "OSS" : "LOCAL");
        entity.setDelFlag(FileBizConstants.DEL_FLAG_NORMAL);
        entity.setCreateBy(SecurityContextUtils.getUsername());
        entity.setCreateTime(LocalDateTime.now());
        if (uploadDTO != null) {
            entity.setBizType(uploadDTO.getBizType());
            entity.setBizId(uploadDTO.getBizId());
            entity.setRemark(uploadDTO.getRemark());
            entity.setCreateByUserId(uploadDTO.getCreateByUserId());
            entity.setCreateDeptId(uploadDTO.getCreateDeptId());
            if (StringUtils.hasText(uploadDTO.getFileHash())) {
                entity.setFileHash(uploadDTO.getFileHash());
            }
        }
        if (entity.getCreateByUserId() == null) {
            entity.setCreateByUserId(SecurityContextUtils.getUserId());
        }
        return entity;
    }

    private SysFileFolder requireActiveFolder(Long folderId) {
        SysFileFolder folder = sysFileFolderMapper.selectById(folderId);
        AssertUtils.notNull(folder, ErrorCode.NOT_FOUND, "文件夹不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(folder.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件夹不存在");
        }
        return folder;
    }

    private void assertAccessible(SysFile file, DataScope dataScope) {
        if (!FileDataScopeSupport.isAccessible(file, dataScope)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权访问该文件");
        }
    }

    private void validateFileSize(long size, FileMediaType mediaType) {
        Long maxSize = fileCenterProperties.getMaxSizeByMediaType()
                .getOrDefault(mediaType.getCode(), mediaType.getDefaultMaxSize());
        if (size > maxSize) {
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

    private String resolveSuffix(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
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
            case DOCUMENT -> {
                if ("pdf".equalsIgnoreCase(fileExt)) {
                    yield "pdf";
                }
                if (isOfficeExt(fileExt)) {
                    yield "office";
                }
                yield "download";
            }
            default -> "download";
        };
    }

    private boolean isOfficeExt(String fileExt) {
        if (!StringUtils.hasText(fileExt)) {
            return false;
        }
        Set<String> exts = fileCenterProperties.getPreview() != null
                ? fileCenterProperties.getPreview().getOfficeExtensions()
                : null;
        if (exts == null || exts.isEmpty()) {
            return false;
        }
        return exts.contains(fileExt.toLowerCase(Locale.ROOT));
    }

    private SysFile getActiveFile(Long fileId) {
        if (fileId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFileId, fileId)
                .eq(SysFile::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
    }

    /** 内存 MultipartFile，用于先算 hash 再上传 */
    private static final class InMemoryMultipartFile implements MultipartFile {
        private final String name;
        private final String contentType;
        private final byte[] content;

        private InMemoryMultipartFile(String name, String contentType, byte[] content) {
            this.name = name;
            this.contentType = contentType;
            this.content = content != null ? content : new byte[0];
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return name;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws java.io.IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}
