package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.BrandCategoryBindBo;
import com.star.pivot.mall.domain.bo.BrandReqBo;
import com.star.pivot.mall.domain.bo.BrandSaveBo;
import com.star.pivot.mall.domain.vo.BrandCategoryVo;
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
@Tag(name = "商城-品牌", description = "品牌 CRUD（表 pms_brand）")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "品牌分页列表", description = "支持名称模糊、显示状态、首字母筛选")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('mall:brand:query')")
    public Result<PageResponse<BrandVo>> pageList(@RequestBody BrandReqBo brandReqBo) {
        return Result.success(brandService.pageList(brandReqBo));
    }

    @Operation(summary = "品牌详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mall:brand:query')")
    public Result<BrandVo> getById(@PathVariable("id") Long id) {
        return Result.success(brandService.getById(id));
    }

    @Log(title = "新增品牌", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "新增品牌")
    @PostMapping
    @PreAuthorize("hasAuthority('mall:brand:add')")
    public Result<?> add(@Valid @RequestBody BrandSaveBo bo) {
        brandService.addBrand(bo);
        return Result.success("新增成功");
    }

    @Log(title = "修改品牌", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "修改品牌")
    @PutMapping
    @PreAuthorize("hasAuthority('mall:brand:edit')")
    public Result<?> update(@Valid @RequestBody BrandSaveBo bo) {
        brandService.updateBrand(bo);
        return Result.success("修改成功");
    }

    @Log(title = "删除品牌", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除品牌", description = "请求体 ids 为品牌主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('mall:brand:delete')")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        brandService.removeByIds(ids);
        return Result.success("删除成功");
    }

    @Operation(summary = "品牌已绑定的三级分类列表")
    @GetMapping("/{id}/categories")
    @PreAuthorize("hasAuthority('mall:brand:query')")
    public Result<List<BrandCategoryVo>> listBoundCategories(@PathVariable("id") Long id) {
        return Result.success(brandService.listBoundCategories(id));
    }

    @Log(title = "品牌绑定分类", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "品牌绑定三级分类", description = "全量覆盖；catIds 为空则清空绑定")
    @PutMapping("/categories")
    @PreAuthorize("hasAuthority('mall:brand:edit')")
    public Result<?> bindCategories(@Valid @RequestBody BrandCategoryBindBo bo) {
        brandService.bindCategories(bo);
        return Result.success("绑定成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
