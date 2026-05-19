package com.star.pivot.mall.controller;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.mall.domain.bo.SkuReqBo;
import com.star.pivot.mall.domain.vo.SkuVo;
import com.star.pivot.mall.service.PmsSkuInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** SKU 只读查询（增删改在 SPU 发布向导中完成） */
@RestController
@RequestMapping("/mall/sku")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-SKU", description = "SKU 只读查询（表 pms_sku_info）")
public class PmsSkuInfoController {

    private final PmsSkuInfoService pmsSkuInfoService;

    @Operation(summary = "SKU 分页列表")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('mall:product:query')")
    public Result<PageResponse<SkuVo>> pageList(@RequestBody SkuReqBo reqBo) {
        return Result.success(pmsSkuInfoService.getSkuPageList(reqBo));
    }
}
