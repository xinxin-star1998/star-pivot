package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.CategorySaveBo;
import com.star.pivot.mall.domain.bo.CategorySortBatchBo;
import com.star.pivot.mall.domain.vo.CategoryTreeVo;
import com.star.pivot.mall.service.PmsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mall/category")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-分类", description = "分类（表 pms_category）")
public class PmsCategoryController {

    private final PmsCategoryService pmsCategoryService;

    @Operation(summary = "分类树", description = "按 sort、cat_id 排序后组装父子树；根节点 parent_cid 为 0 或空（常见三级：一/二/三级类目）")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('mall:category:query')")
    public Result<List<CategoryTreeVo>> tree() {
        return Result.success(pmsCategoryService.treeList());
    }

    @Operation(
            summary = "子分类列表",
            description = "仅返回指定父节点下的直接子节点；parentCid 省略或为 0 时返回一级类目（parent_cid 为 0 或空）")
    @GetMapping("/children")
    @PreAuthorize("hasAuthority('mall:category:query')")
    public Result<List<CategoryTreeVo>> children(
            @RequestParam(value = "parentCid", required = false) Long parentCid) {
        return Result.success(pmsCategoryService.listChildren(parentCid));
    }

    @Operation(summary = "分类详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mall:category:query')")
    public Result<CategoryTreeVo> getById(@PathVariable("id") Long id) {
        return Result.success(pmsCategoryService.getDetail(id));
    }

    @Log(title = "新增商品分类", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("hasAuthority('mall:category:add')")
    public Result<?> add(@Valid @RequestBody CategorySaveBo bo) {
        pmsCategoryService.addCategory(bo);
        return Result.success("新增成功");
    }

    @Log(title = "修改商品分类", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "修改分类")
    @PutMapping
    @PreAuthorize("hasAuthority('mall:category:edit')")
    public Result<?> update(@Valid @RequestBody CategorySaveBo bo) {
        pmsCategoryService.updateCategory(bo);
        return Result.success("修改成功");
    }

    @Log(title = "删除商品分类", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除分类", description = "请求体 ids 为分类主键列表；存在子分类时不允许删除")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('mall:category:delete')")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        pmsCategoryService.removeCategories(ids);
        return Result.success("删除成功");
    }

    @Log(title = "更新商品分类排序", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(
            summary = "批量更新同级排序",
            description = "拖拽排序后提交同一父级下各节点的 sort；不允许跨父级调整")
    @PutMapping("/sort")
    @PreAuthorize("hasAuthority('mall:category:edit')")
    public Result<?> updateSort(@Valid @RequestBody CategorySortBatchBo bo) {
        pmsCategoryService.updateSortBatch(bo.getItems());
        return Result.success("排序已更新");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
