package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.vo.ProductVo;
import com.star.pivot.mall.service.PmsSpuInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mall/product")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-SPU", description = "SPU CRUD（表 pms_spu_info）")
public class PmsSpuInfoController {

    private final PmsSpuInfoService pmsSpuInfoService;

    @Operation(summary = "SPU 分页列表", description = "支持 SPU 名称模糊，目录、品牌、上架状态筛选")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('mall:product:query')")
    public Result<PageResponse<ProductVo>> pageList(@RequestBody ProductReqBo productReqBo) {
        return Result.success(pmsSpuInfoService.getPmsSpuInfoPageList(productReqBo));
    }

    @Operation(summary = "SPU 详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mall:product:query')")
    public Result<ProductVo> getById(@PathVariable("id") Long id) {
        return Result.success(pmsSpuInfoService.getPmsSpuInfoById(id));
    }

    @Log(title = "新增SPU", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "新增 SPU")
    @PostMapping
    @PreAuthorize("hasAuthority('mall:product:add')")
    public Result<?> add(@Valid @RequestBody ProductSaveBo bo) {
        pmsSpuInfoService.addPmsSpuInfo(bo);
        return Result.success("新增成功");
    }

    @Log(title = "修改SPU", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "修改 SPU")
    @PutMapping
    @PreAuthorize("hasAuthority('mall:product:edit')")
    public Result<?> update(@Valid @RequestBody ProductSaveBo bo) {
        pmsSpuInfoService.updatePmsSpuInfo(bo);
        return Result.success("修改成功");
    }

    @Log(title = "删除SPU", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除 SPU", description = "请求体 ids 为 SPU 主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('mall:product:delete')")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        pmsSpuInfoService.removePmsSpuInfoByIds(ids);
        return Result.success("删除成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
