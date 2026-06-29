package com.star.pivot.controller.file;

import com.star.pivot.file.domain.dto.*;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.service.ISysFileService;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "文件中心", description = "文件上传、列表、回收站、迁移")
public class SysFileController {

    private final ISysFileService sysFileService;

    @PreAuthorize("hasAuthority('file:resource:query')")
    @PostMapping("/list")
    public Result<PageResponse<SysFileVo>> pageList(@Valid @RequestBody SysFileQueryDTO queryDTO) {
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
            @RequestParam(required = false) String remark) {
        SysFileUploadDTO uploadDTO = new SysFileUploadDTO();
        uploadDTO.setBizType(bizType);
        uploadDTO.setBizId(bizId);
        uploadDTO.setRemark(remark);
        return Result.success(sysFileService.upload(file, folderId, uploadDTO));
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @GetMapping("/{fileId}")
    public Result<SysFileVo> detail(@PathVariable Long fileId) {
        return Result.success(sysFileService.getDetail(fileId));
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @GetMapping("/preview-url/{fileId}")
    public Result<Map<String, String>> previewUrl(@PathVariable Long fileId) {
        return Result.success(sysFileService.previewUrl(fileId));
    }

    @Log(title = "删除文件", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:delete')")
    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.logicDelete(ids);
        return Result.success();
    }

    @Log(title = "恢复文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:restore')")
    @PutMapping("/restore")
    public Result<Void> restore(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        sysFileService.restore(ids);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('file:resource:query')")
    @PostMapping("/recycle/list")
    public Result<PageResponse<SysFileVo>> recycleList(@Valid @RequestBody SysFileRecycleQueryDTO queryDTO) {
        return Result.success(sysFileService.recyclePage(queryDTO));
    }

    @Log(title = "迁移文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:move')")
    @PutMapping("/move")
    public Result<Void> move(@Valid @RequestBody SysFileMoveDTO moveDTO) {
        sysFileService.moveToFolder(moveDTO.getIds(), moveDTO.getTargetFolderId());
        return Result.success();
    }

    @Log(title = "重命名文件", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:resource:edit')")
    @PutMapping("/rename")
    public Result<Void> rename(@Valid @RequestBody SysFileRenameDTO renameDTO) {
        sysFileService.rename(renameDTO.getFileId(), renameDTO.getFileName());
        return Result.success();
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "ID列表不能为空");
        }
        return ids;
    }
}
