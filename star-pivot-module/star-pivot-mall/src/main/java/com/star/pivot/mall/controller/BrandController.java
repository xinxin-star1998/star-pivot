package com.star.pivot.mall.controller;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.bo.BrandSaveBo;
import com.star.pivot.mall.domain.vo.BrandVo;
import com.star.pivot.mall.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mall/brand")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-品牌", description = "品牌 CRUD（表 brand，需已建表）")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "品牌分页列表", description = "支持品牌名称模糊、状态精确筛选")
    @PostMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResponse<BrandVo>> pageList(@RequestBody BrandReqBo brandReqBo) {
        return Result.success(brandService.pageList(brandReqBo));
    }

    @Operation(summary = "品牌详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<BrandVo> getById(@PathVariable("id") Long id) {
        return Result.success(brandService.getById(id));
    }

    @Operation(summary = "新增品牌")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> add(@Valid @RequestBody BrandSaveBo bo) {
        brandService.addBrand(bo);
        return Result.success("新增成功");
    }

    @Operation(summary = "修改品牌")
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> update(@Valid @RequestBody BrandSaveBo bo) {
        brandService.updateBrand(bo);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除品牌", description = "请求体 ids 为品牌主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        brandService.removeByIds(ids);
        return Result.success("删除成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
