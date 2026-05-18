package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.mall.domain.dto.GroupAttrRelationSaveDTO;
import com.star.pivot.mall.domain.dto.PmsAttrGroupDTO;
import com.star.pivot.mall.domain.dto.PmsAttrGroupQueryDTO;
import com.star.pivot.mall.domain.excel.PmsAttrGroupExcel;
import com.star.pivot.mall.domain.vo.GroupAttrRelationVO;
import com.star.pivot.mall.domain.vo.PmsAttrGroupVO;
import com.star.pivot.mall.excel.PmsAttrGroupExcelHandler;
import com.star.pivot.mall.service.IPmsAttrGroupService;
import com.star.pivot.framework.excel.ExcelImportOptions;
import com.star.pivot.framework.excel.ExcelToolkit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 属性分组控制器
 * 
 * @author admin
 * @since 2026-05-18
 */
@RestController
@RequestMapping("/mall/group")
@RequiredArgsConstructor
public class PmsAttrGroupController
{
    private final IPmsAttrGroupService pmsAttrGroupService;
    private final PmsAttrGroupExcelHandler pmsAttrGroupExcelHandler;

    /**
     * 分页查询属性分组列表
     * 
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @PreAuthorize("hasAuthority('mall:group:query')")
    @PostMapping("/list")
    public Result<PageResponse<PmsAttrGroupVO>> list(@RequestBody PmsAttrGroupQueryDTO queryDTO)
    {
        PageResponse<PmsAttrGroupVO> page = pmsAttrGroupService.selectPmsAttrGroupPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取属性分组详细信息
     * 
     * @param attrGroupId 属性分组主键
     * @return 属性分组信息
     */
    @PreAuthorize("hasAuthority('mall:group:query')")
    @GetMapping(value = "/{attrGroupId}")
    public Result<PmsAttrGroupVO> getInfo(@PathVariable("attrGroupId") Long attrGroupId)
    {
        PmsAttrGroupVO pmsAttrGroupVO = pmsAttrGroupService.selectPmsAttrGroupByAttrGroupId(attrGroupId);
        return Result.success(pmsAttrGroupVO);
    }

    /**
     * 新增属性分组
     * 
     * @param pmsAttrGroupDTO 属性分组信息
     * @return 操作结果
     */
    @Log(title = "新增属性分组", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('mall:group:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody PmsAttrGroupDTO pmsAttrGroupDTO)
    {
        boolean success = pmsAttrGroupService.insertPmsAttrGroup(pmsAttrGroupDTO);
        return success ? Result.success("新增属性分组成功") : Result.error("新增属性分组失败");
    }

    /**
     * 修改属性分组
     * 
     * @param pmsAttrGroupDTO 属性分组信息
     * @return 操作结果
     */
    @Log(title = "修改属性分组", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('mall:group:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody PmsAttrGroupDTO pmsAttrGroupDTO)
    {
        boolean success = pmsAttrGroupService.updatePmsAttrGroup(pmsAttrGroupDTO);
        return success ? Result.success("修改属性分组成功") : Result.error("修改属性分组失败");
    }

    /** 查询分组可关联的基本属性列表（含是否已关联） */
    @PreAuthorize("hasAuthority('mall:group:query')")
    @GetMapping("/{attrGroupId}/attrs")
    public Result<List<GroupAttrRelationVO>> listGroupAttrs(@PathVariable Long attrGroupId) {
        return Result.success(pmsAttrGroupService.listGroupAttrRelations(attrGroupId));
    }

    /** 保存分组关联的基本属性（全量覆盖本分组） */
    @Log(title = "保存属性分组关联", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('mall:group:edit')")
    @PutMapping("/{attrGroupId}/attrs")
    public Result<?> saveGroupAttrs(
            @PathVariable Long attrGroupId, @Valid @RequestBody GroupAttrRelationSaveDTO saveDTO) {
        boolean success = pmsAttrGroupService.saveGroupAttrRelations(attrGroupId, saveDTO);
        return success ? Result.success("关联属性保存成功") : Result.error("关联属性保存失败");
    }
    /**
     * 删除属性分组（支持批量删除，请求体 ids）
     * 
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果
     */
    @Log(title = "删除属性分组", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('mall:group:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest)
    {
        List<Long> idList = deleteRequest.getIds();
        if (idList == null || idList.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        Long[] attrGroupIds = idList.toArray(new Long[0]);
        boolean success = pmsAttrGroupService.deletePmsAttrGroupByAttrGroupIds(attrGroupIds);
        return success ? Result.success("删除属性分组成功") : Result.error("删除属性分组失败");
    }

    /** EasyExcel 导出属性分组 */
    @Log(title = "导出属性分组", businessType = AppConstants.BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('mall:group:export')")
    @NoResponseWrapper
    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody(required = false) PmsAttrGroupQueryDTO queryDTO) {
        return ExcelToolkit.export(pmsAttrGroupExcelHandler, queryDTO, PmsAttrGroupExcel.class);
    }

    /** EasyExcel 导入属性分组 */
    @Log(title = "导入属性分组", businessType = AppConstants.BusinessType.IMPORT)
    @PreAuthorize("hasAuthority('mall:group:import')")
    @PostMapping("/import")
    public Result<?> importData(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateSupport", defaultValue = "false") boolean updateSupport)
            throws Exception {
        return ExcelToolkit.importFile(
                file,
                pmsAttrGroupExcelHandler,
                ExcelImportOptions.of(updateSupport),
                PmsAttrGroupExcel.class);
    }

    /** EasyExcel 下载导入模板 */
    @PreAuthorize("hasAuthority('mall:group:import')")
    @NoResponseWrapper
    @GetMapping("/importTemplate")
    public ResponseEntity<?> importTemplate() {
        return ExcelToolkit.downloadTemplate(
                pmsAttrGroupExcelHandler, null, PmsAttrGroupExcel.class);
    }
}
