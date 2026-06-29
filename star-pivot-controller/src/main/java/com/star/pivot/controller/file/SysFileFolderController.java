package com.star.pivot.controller.file;

import com.star.pivot.file.domain.dto.SysFileFolderDTO;
import com.star.pivot.file.domain.vo.FileCategoryNodeVo;
import com.star.pivot.file.service.ISysFileFolderService;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file/folder")
@RequiredArgsConstructor
@Tag(name = "文件中心文件夹", description = "文件夹树、新建、编辑、删除")
public class SysFileFolderController {

    private final ISysFileFolderService sysFileFolderService;

    @PreAuthorize("hasAuthority('file:folder:query')")
    @GetMapping("/tree")
    public Result<List<FileCategoryNodeVo>> tree(@RequestParam(required = false) String category) {
        return Result.success(sysFileFolderService.listTree(category));
    }

    @Log(title = "新建文件夹", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:folder:add')")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SysFileFolderDTO dto) {
        return Result.success(sysFileFolderService.create(dto));
    }

    @Log(title = "编辑文件夹", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('file:folder:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysFileFolderDTO dto) {
        sysFileFolderService.update(dto);
        return Result.success();
    }

    @Log(title = "删除文件夹", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:folder:delete')")
    @DeleteMapping("/{folderId}")
    public Result<Void> delete(@PathVariable Long folderId) {
        sysFileFolderService.delete(folderId);
        return Result.success();
    }
}
