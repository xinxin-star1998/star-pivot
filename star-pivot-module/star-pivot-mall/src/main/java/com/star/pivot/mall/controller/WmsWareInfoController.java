package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.WmsWareInfoReqBo;
import com.star.pivot.mall.domain.bo.WmsWareInfoSaveBo;
import com.star.pivot.mall.domain.vo.WmsWareInfoVo;
import com.star.pivot.mall.service.WmsWareInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓储服务 -- 仓库管理
 */
@RestController
@RequestMapping("/mall/wareinfo")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-仓库", description = "仓库 CRUD（表 wms_ware_info）")
public class WmsWareInfoController {

    private final WmsWareInfoService wmsWareInfoService;

    @Operation(summary = "仓库分页列表")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('mall:ware:query')")
    public Result<PageResponse<WmsWareInfoVo>> pageList(@RequestBody WmsWareInfoReqBo wmsWareInfoReqBo) {
        return Result.success(wmsWareInfoService.getWmsWareInfoPageList(wmsWareInfoReqBo));
    }

    @Operation(summary = "仓库详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mall:ware:query')")
    public Result<WmsWareInfoVo> getById(@PathVariable("id") Long id) {
        return Result.success(wmsWareInfoService.getById(id));
    }

    @Log(title = "新增仓库", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "新增仓库")
    @PostMapping
    @PreAuthorize("hasAuthority('mall:ware:add')")
    public Result<?> add(@Valid @RequestBody WmsWareInfoSaveBo bo) {
        wmsWareInfoService.addWare(bo);
        return Result.success("新增成功");
    }

    @Log(title = "修改仓库", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "修改仓库")
    @PutMapping
    @PreAuthorize("hasAuthority('mall:ware:edit')")
    public Result<?> update(@Valid @RequestBody WmsWareInfoSaveBo bo) {
        wmsWareInfoService.updateWare(bo);
        return Result.success("修改成功");
    }

    @Log(title = "删除仓库", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除仓库", description = "请求体 ids 为仓库主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('mall:ware:delete')")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        wmsWareInfoService.removeByIds(ids);
        return Result.success("删除成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
