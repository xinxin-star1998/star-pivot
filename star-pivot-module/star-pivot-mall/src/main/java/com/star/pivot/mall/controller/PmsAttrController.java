package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.excel.ExcelImportOptions;
import com.star.pivot.framework.excel.ExcelToolkit;
import com.star.pivot.mall.domain.dto.PmsAttrDTO;
import com.star.pivot.mall.domain.dto.PmsAttrQueryDTO;
import com.star.pivot.mall.domain.excel.PmsAttrExcel;
import com.star.pivot.mall.domain.vo.PmsAttrVO;
import com.star.pivot.mall.excel.PmsAttrExcelHandler;
import com.star.pivot.mall.service.IPmsAttrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品属性接口。
 * <p>
 * 基本属性、销售属性共用本 Controller，通过请求体 {@code attrType} 区分：
 * 1-基本属性（mall:base:*），0-销售属性（mall:sale:*）。
 * <p>
 * 与属性分组的绑定见 DTO 的 {@code attrGroupId}，由 Service 写入
 * {@code pms_attr_attrgroup_relation}，不在 pms_attr 表存分组 id。
 */
@RestController
@RequestMapping("/mall/attr")
@RequiredArgsConstructor
public class PmsAttrController {

    private final IPmsAttrService pmsAttrService;
    private final PmsAttrExcelHandler pmsAttrExcelHandler;

    /**
     * 分页列表。queryDTO.attrType 必填；attrGroupId 仅作展示回填，不作为列表筛选条件。
     */
    @PreAuthorize(
            "hasAnyAuthority('mall:base:query', 'mall:sale:query', 'mall:product:query', 'mall:product:add', 'mall:product:edit')")
    @PostMapping("/list")
    public Result<PageResponse<PmsAttrVO>> list(@RequestBody PmsAttrQueryDTO queryDTO) {
        PageResponse<PmsAttrVO> page = pmsAttrService.selectPmsAttrPage(queryDTO);
        return Result.success(page);
    }

    /** 详情（含关联表中的 attrGroupId、attrSort）。 */
    @PreAuthorize("hasAnyAuthority('mall:base:query', 'mall:sale:query')")
    @GetMapping("/{attrId}")
    public Result<PmsAttrVO> getInfo(@PathVariable("attrId") Long attrId) {
        PmsAttrVO pmsAttrVO = pmsAttrService.selectPmsAttrByAttrId(attrId);
        return Result.success(pmsAttrVO);
    }

    /** 新增；可选传 attrGroupId、attrSort 建立分组关联。 */
    @Log(title = "新增商品属性", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAnyAuthority('mall:base:add', 'mall:sale:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody PmsAttrDTO pmsAttrDTO) {
        boolean success = pmsAttrService.insertPmsAttr(pmsAttrDTO);
        return success ? Result.success("新增商品属性成功") : Result.error("新增商品属性失败");
    }

    /** 修改；传 attrGroupId=null 可解除与分组的关联。 */
    @Log(title = "修改商品属性", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('mall:base:edit', 'mall:sale:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody PmsAttrDTO pmsAttrDTO) {
        boolean success = pmsAttrService.updatePmsAttr(pmsAttrDTO);
        return success ? Result.success("修改商品属性成功") : Result.error("修改商品属性失败");
    }

    /** 批量删除（同时删除 pms_attr_attrgroup_relation 中对应行）。 */
    @Log(title = "删除商品属性", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAnyAuthority('mall:base:delete', 'mall:sale:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> idList = deleteRequest.getIds();
        if (idList == null || idList.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        Long[] attrIds = idList.toArray(new Long[0]);
        boolean success = pmsAttrService.deletePmsAttrByAttrIds(attrIds);
        return success ? Result.success("删除商品属性成功") : Result.error("删除商品属性失败");
    }

    /** EasyExcel 导出商品属性（请求体须含 attrType） */
    @Log(title = "导出商品属性", businessType = AppConstants.BusinessType.EXPORT)
    @PreAuthorize("hasAnyAuthority('mall:base:export', 'mall:sale:export')")
    @NoResponseWrapper
    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody PmsAttrQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getAttrType() == null) {
            return ResponseEntity.badRequest().body(Result.error("导出参数 attrType 不能为空"));
        }
        return ExcelToolkit.export(pmsAttrExcelHandler, queryDTO, PmsAttrExcel.class);
    }

    /** EasyExcel 导入商品属性 */
    @Log(title = "导入商品属性", businessType = AppConstants.BusinessType.IMPORT)
    @PreAuthorize("hasAnyAuthority('mall:base:import', 'mall:sale:import')")
    @PostMapping("/import")
    public Result<?> importData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("attrType") Integer attrType,
            @RequestParam(value = "updateSupport", defaultValue = "false") boolean updateSupport)
            throws Exception {
        if (attrType == null) {
            return Result.error("attrType 不能为空");
        }
        ExcelImportOptions options =
                ExcelImportOptions.builder().updateSupport(updateSupport).param("attrType", attrType).build();
        return ExcelToolkit.importFile(file, pmsAttrExcelHandler, options, PmsAttrExcel.class);
    }

    /** EasyExcel 下载导入模板 */
    @PreAuthorize("hasAnyAuthority('mall:base:import', 'mall:sale:import')")
    @NoResponseWrapper
    @GetMapping("/importTemplate")
    public ResponseEntity<?> importTemplate(@RequestParam("attrType") Integer attrType) {
        if (attrType == null) {
            return ResponseEntity.badRequest().body(Result.error("attrType 不能为空"));
        }
        PmsAttrQueryDTO query = new PmsAttrQueryDTO();
        query.setAttrType(attrType);
        return ExcelToolkit.downloadTemplate(pmsAttrExcelHandler, query, PmsAttrExcel.class);
    }
}
