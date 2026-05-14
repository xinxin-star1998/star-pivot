package com.star.pivot.mall.controller;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.ProductReqBo;
import com.star.pivot.mall.domain.bo.ProductSaveBo;
import com.star.pivot.mall.domain.vo.ProductVo;
import com.star.pivot.mall.service.ProductService;
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
@RequestMapping("/mall/product")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-商品", description = "商品 CRUD（表 product，需已建表）")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页列表", description = "支持名称模糊，分类、品牌、状态筛选")
    @PostMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResponse<ProductVo>> pageList(@RequestBody ProductReqBo productReqBo) {
        return Result.success(productService.pageList(productReqBo));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<ProductVo> getById(@PathVariable("id") Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> add(@Valid @RequestBody ProductSaveBo bo) {
        productService.addProduct(bo);
        return Result.success("新增成功");
    }

    @Operation(summary = "修改商品")
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> update(@Valid @RequestBody ProductSaveBo bo) {
        productService.updateProduct(bo);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除商品", description = "请求体 ids 为主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        productService.removeByIds(ids);
        return Result.success("删除成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
