package com.star.pivot.controller.file;

import com.star.pivot.file.domain.dto.*;
import com.star.pivot.file.domain.vo.SysFileAuditVo;
import com.star.pivot.file.domain.vo.SysFileHashCheckVo;
import com.star.pivot.file.domain.vo.SysFileMultipartInitVo;
import com.star.pivot.file.domain.vo.SysFileTagVo;
import com.star.pivot.file.domain.vo.SysFileUsageSummaryVo;
import com.star.pivot.file.domain.vo.SysFileVersionVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.domain.vo.SysFileWatermarkVo;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.file.service.ISysFileMetaService;
import com.star.pivot.file.service.ISysFileService;
import com.star.pivot.file.service.ISysFileVersionService;
import com.star.pivot.file.service.ISysFileWatermarkService;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.utils.DataScopeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "文件中心", description = "文件上传、列表、回收站、迁移、分片、秒传、收藏、标签、打包下载")
public class SysFileController {

    private final ISysFileService sysFileService;
    private final ISysFileMetaService sysFileMetaService;
    private final ISysFileVersionService sysFileVersionService;
    private final ISysFileAuditService sysFileAuditService;
    private final ISysFileWatermarkService sysFileWatermarkService;
    private final DataScopeService dataScopeService;
    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;

    @PreAuthorize("hasAuthority('file:resource:query')")
    @PostMapping("/list")
    public Result<PageResponse<SysFileVo>> pageList(@Valid @RequestBody SysFileQueryDTO queryDTO) {
        applyDataScope(queryDTO);
        queryDTO.setCurrentUserId(SecurityContextUtils.getUserId());
        return Result.success(sysFileService.pageList(queryDTO));
    }

    @Log(title = "上传文件", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:resource:add')")
    @PostMapping("/upload")
    public Result<SysFileVo> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderId") Long folderId,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String bizId,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String fileHash) {
        SysFileUploadDTO uploadDTO = buildOwnerUploadDTO();
        uploadDTO.setBizType(bizType);
        uploadDTO.setBizId(bizId);
        uploadDTO.setRemark(remark);
        uploadDTO.setFileHash(fileHash);
        return Result.success(sysFileService.upload(file, folderId, uploadDTO));
    }

    @Log(title = "秒传校验", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:resource:add')")
    @PostMapping("/check-hash")
    public Result<SysFileHashCheckVo> checkHash(@Valid @RequestBody SysFileHashCheckDTO dto) {
        return Result.success(sysFileService.checkHashAndInstantUpload(dto, buildOwnerUploadDTO()));
    }

    @PreAuthorize("hasAuthority('file:resource:add')")
    @PostMapping("/multipart/init")
    public Result<SysFileMultipartInitVo> initMultipart(@Valid @RequestBody SysFileMultipartInitDTO dto) {
        return Result.success(sysFileService.initMultipart(dto, buildOwnerUploadDTO()));
    }

    @PreAuthorize("hasAuthority('file:resource:add')")
    @PostMapping("/multipart/part")
    public Result<Map<String, String>> uploadPart(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("objectName") String objectName,
            @RequestParam("partNumber") int partNumber,
            @RequestParam("file") MultipartFile chunk) {
        return Result.success(sysFileService.uploadMultipartPart(uploadId, objectName, partNumber, chunk));
    }

    @Log(title = "完成分片上传", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:resource:add')")
    @PostMapping("/multipart/complete")
    public Result<SysFileVo> completeMultipart(@Valid @RequestBody SysFileMultipartCompleteDTO dto) {
        return Result.success(sysFileService.completeMultipart(dto, buildOwnerUploadDTO()));
    }

    @PreAuthorize("hasAuthority('file:resource:add')")
    @DeleteMapping("/multipart/abort")
    public Result<Void> abortMultipart(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("objectName") String objectName) {
        sysFileService.abortMultipart(uploadId, objectName);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:add')")
    @GetMapping("/multipart/status")
    public Result<SysFileMultipartInitVo> multipartStatus(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("objectName") String objectName) {
        return Result.success(sysFileService.multipartStatus(uploadId, objectName));
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @GetMapping("/{fileId}")
    public Result<SysFileVo> detail(@PathVariable Long fileId) {
        return Result.success(sysFileService.getDetail(fileId, currentDataScope()));
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @GetMapping("/preview-url/{fileId}")
    public Result<Map<String, String>> previewUrl(@PathVariable Long fileId) {
        return Result.success(sysFileService.previewUrl(fileId, currentDataScope()));
    }

    @Log(title = "删除文件", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:delete')")
    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.logicDelete(ids, currentDataScope());
        return Result.success();
    }

    @Log(title = "恢复文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:restore')")
    @PutMapping("/restore")
    public Result<Void> restore(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.restore(ids, currentDataScope());
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @PostMapping("/recycle/list")
    public Result<PageResponse<SysFileVo>> recycleList(@Valid @RequestBody SysFileRecycleQueryDTO queryDTO) {
        applyDataScope(queryDTO);
        return Result.success(sysFileService.recyclePage(queryDTO));
    }

    @Log(title = "彻底删除文件", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:purge')")
    @DeleteMapping("/purge")
    public Result<Void> purge(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.purge(ids, currentDataScope());
        return Result.success();
    }

    @Log(title = "清空回收站", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:purge')")
    @DeleteMapping("/recycle/clear")
    public Result<Integer> clearRecycle() {
        return Result.success(sysFileService.clearRecycle(currentDataScope()));
    }

    @Log(title = "迁移文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:move')")
    @PutMapping("/move")
    public Result<Void> move(@Valid @RequestBody SysFileMoveDTO moveDTO) {
        sysFileService.moveToFolder(moveDTO.getIds(), moveDTO.getTargetFolderId(), currentDataScope());
        return Result.success();
    }

    @Log(title = "重命名文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:edit')")
    @PutMapping("/rename")
    public Result<Void> rename(@Valid @RequestBody SysFileRenameDTO renameDTO) {
        sysFileService.rename(renameDTO.getFileId(), renameDTO.getFileName(), currentDataScope());
        return Result.success();
    }

    @Log(title = "收藏文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:query')")
    @PutMapping("/favorite/{fileId}")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Long fileId) {
        boolean favorited = sysFileMetaService.toggleFavorite(fileId, currentDataScope());
        return Result.success(Map.of("favorited", favorited));
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @PutMapping("/recent/{fileId}")
    public Result<Void> touchRecent(@PathVariable Long fileId) {
        sysFileMetaService.touchRecent(fileId, currentDataScope());
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:tag')")
    @GetMapping("/tag/list")
    public Result<List<SysFileTagVo>> listTags() {
        return Result.success(sysFileMetaService.listMyTags());
    }

    @Log(title = "新建标签", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:resource:tag')")
    @PostMapping("/tag")
    public Result<SysFileTagVo> createTag(@Valid @RequestBody SysFileTagDTO dto) {
        return Result.success(sysFileMetaService.createTag(dto));
    }

    @Log(title = "更新标签", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:tag')")
    @PutMapping("/tag")
    public Result<SysFileTagVo> updateTag(@Valid @RequestBody SysFileTagDTO dto) {
        return Result.success(sysFileMetaService.updateTag(dto));
    }

    @Log(title = "删除标签", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:tag')")
    @DeleteMapping("/tag/{tagId}")
    public Result<Void> deleteTag(@PathVariable Long tagId) {
        sysFileMetaService.deleteTag(tagId);
        return Result.success();
    }

    @Log(title = "文件打标", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:tag')")
    @PutMapping("/tag/bind")
    public Result<Void> bindTags(@Valid @RequestBody SysFileTagBindDTO dto) {
        sysFileMetaService.bindTags(dto, currentDataScope());
        return Result.success();
    }

    @Log(title = "取消打标", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:tag')")
    @PutMapping("/tag/unbind")
    public Result<Void> unbindTags(@Valid @RequestBody SysFileTagBindDTO dto) {
        sysFileMetaService.unbindTags(dto, currentDataScope());
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @GetMapping("/watermark/config")
    public Result<SysFileWatermarkVo> watermarkConfig() {
        return Result.success(sysFileWatermarkService.currentConfig());
    }

    @Log(title = "水印下载", businessType = AppConstants.BusinessType.EXPORT)
    @PreAuthorize("hasAnyAuthority('file:resource:download', 'file:resource:query')")
    @NoResponseWrapper
    @GetMapping("/download/watermarked/{fileId}")
    public void downloadWatermarked(@PathVariable Long fileId, HttpServletResponse response) {
        sysFileWatermarkService.downloadWatermarked(fileId, currentDataScope(), response);
    }

    @Log(title = "批量打包下载", businessType = AppConstants.BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('file:resource:download')")
    @NoResponseWrapper
    @PostMapping("/download/zip")
    public void downloadZip(@RequestBody DeleteRequest deleteRequest, HttpServletResponse response) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.downloadZip(ids, currentDataScope(), response);
    }

    @PreAuthorize("hasAuthority('file:resource:version')")
    @GetMapping("/{fileId}/versions")
    public Result<List<SysFileVersionVo>> listVersions(@PathVariable Long fileId) {
        return Result.success(sysFileVersionService.listVersions(fileId, currentDataScope()));
    }

    @Log(title = "上传文件新版本", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:version')")
    @PostMapping("/{fileId}/version")
    public Result<SysFileVo> uploadVersion(
            @PathVariable Long fileId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String remark) {
        return Result.success(sysFileVersionService.uploadVersion(fileId, file, remark, currentDataScope()));
    }

    @Log(title = "恢复文件版本", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:version')")
    @PostMapping("/{fileId}/versions/{versionId}/restore")
    public Result<SysFileVo> restoreVersion(@PathVariable Long fileId, @PathVariable Long versionId) {
        return Result.success(sysFileVersionService.restoreVersion(fileId, versionId, currentDataScope()));
    }

    @Log(title = "删除文件版本", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:version')")
    @DeleteMapping("/{fileId}/versions/{versionId}")
    public Result<Void> deleteVersion(@PathVariable Long fileId, @PathVariable Long versionId) {
        sysFileVersionService.deleteVersion(fileId, versionId, currentDataScope());
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:audit')")
    @PostMapping("/audit/list")
    public Result<PageResponse<SysFileAuditVo>> auditList(@Valid @RequestBody SysFileAuditQueryDTO queryDTO) {
        return Result.success(sysFileAuditService.pageList(queryDTO));
    }

    @PreAuthorize("hasAuthority('file:resource:stats')")
    @GetMapping("/stats/usage")
    public Result<SysFileUsageSummaryVo> usageStats(
            @RequestParam(defaultValue = "user") String groupBy) {
        SysFileUsageQueryDTO query = new SysFileUsageQueryDTO();
        query.setGroupBy(groupBy);
        FileDataScopeSupport.applyToQuery(currentDataScope(),
                query::setDataScope, query::setDeptIds, query::setUserId, query::setUserDeptId);
        SysFileUsageSummaryVo summary = sysFileService.usageStats(query);
        if ("dept".equalsIgnoreCase(groupBy) && summary.getItems() != null) {
            enrichDeptNames(summary);
        }
        return Result.success(summary);
    }

    private void enrichDeptNames(SysFileUsageSummaryVo summary) {
        List<Long> deptIds = summary.getItems().stream()
                .map(item -> item.getGroupId())
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (deptIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = sysDeptMapper.selectBatchIds(deptIds).stream()
                .collect(java.util.stream.Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName, (a, b) -> a));
        summary.getItems().forEach(item -> {
            if (item.getGroupId() != null && nameMap.containsKey(item.getGroupId())) {
                item.setGroupName(nameMap.get(item.getGroupId()));
            }
        });
    }

    private DataScope currentDataScope() {
        return dataScopeService.getCurrentUserDataScope();
    }

    private void applyDataScope(SysFileQueryDTO queryDTO) {
        DataScope scope = currentDataScope();
        FileDataScopeSupport.applyToQuery(scope,
                queryDTO::setDataScope, queryDTO::setDeptIds, queryDTO::setUserId, queryDTO::setUserDeptId);
    }

    private void applyDataScope(SysFileRecycleQueryDTO queryDTO) {
        DataScope scope = currentDataScope();
        FileDataScopeSupport.applyToQuery(scope,
                queryDTO::setDataScope, queryDTO::setDeptIds, queryDTO::setUserId, queryDTO::setUserDeptId);
    }

    private SysFileUploadDTO buildOwnerUploadDTO() {
        SysFileUploadDTO dto = new SysFileUploadDTO();
        Long userId = SecurityContextUtils.getUserId();
        dto.setCreateByUserId(userId);
        if (userId != null) {
            dto.setCreateDeptId(sysUserMapper.selectDeptIdByUserId(userId));
        }
        return dto;
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "ID列表不能为空");
        }
        return ids;
    }
}
