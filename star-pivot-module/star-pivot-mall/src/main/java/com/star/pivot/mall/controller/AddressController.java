package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.mall.domain.bo.AddressVO;
import com.star.pivot.mall.domain.dto.AddressDTO;
import com.star.pivot.mall.domain.dto.AddressQueryDTO;
import com.star.pivot.mall.service.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 省市区地址管理（表 address）
 */
@RestController
@RequestMapping("/mall/address")
@RequiredArgsConstructor
@Validated
@Tag(name = "商城-省市区", description = "省市区地址 CRUD（表 address）")
public class AddressController {

    private final IAddressService addressService;

    @Operation(
            summary = "懒加载子节点",
            description = "仅返回指定父级编码下的直接子级；parentCode 省略或为 0 时返回省级")
    @GetMapping("/children")
    @PreAuthorize("hasAuthority('mall:address:query')")
    public Result<List<AddressVO>> children(
            @RequestParam(value = "parentCode", required = false, defaultValue = "0") String parentCode) {
        return Result.success(addressService.listChildren(parentCode));
    }

    @Operation(summary = "搜索地区", description = "扁平列表，最多 200 条；至少传 name/code/parentCode/level 之一")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('mall:address:query')")
    public Result<List<AddressVO>> search(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String parentCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long level) {
        AddressQueryDTO queryDTO = new AddressQueryDTO();
        queryDTO.setCode(code);
        queryDTO.setParentCode(parentCode);
        queryDTO.setName(name);
        queryDTO.setLevel(level);
        return Result.success(addressService.searchAddress(queryDTO));
    }

    @Operation(summary = "省市区详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mall:address:query')")
    public Result<AddressVO> getInfo(@PathVariable("id") Long id) {
        return Result.success(addressService.selectAddressById(id));
    }

    @Log(title = "新增省市区", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "新增省市区")
    @PostMapping
    @PreAuthorize("hasAuthority('mall:address:add')")
    public Result<?> add(@Valid @RequestBody AddressDTO addressDTO) {
        addressService.insertAddress(addressDTO);
        return Result.success("新增成功");
    }

    @Log(title = "修改省市区", businessType = AppConstants.BusinessType.UPDATE)
    @Operation(summary = "修改省市区")
    @PutMapping
    @PreAuthorize("hasAuthority('mall:address:edit')")
    public Result<?> edit(@Valid @RequestBody AddressDTO addressDTO) {
        addressService.updateAddress(addressDTO);
        return Result.success("修改成功");
    }

    @Log(title = "删除省市区", businessType = AppConstants.BusinessType.DELETE)
    @Operation(summary = "删除省市区", description = "请求体 ids 为地址主键列表")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAuthority('mall:address:delete')")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> ids = validateIds(deleteRequest.getIds());
        addressService.deleteAddressByIds(ids);
        return Result.success("删除成功");
    }

    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
