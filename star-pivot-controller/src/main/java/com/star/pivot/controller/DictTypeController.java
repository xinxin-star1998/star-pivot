package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.DictTypeVO;
import com.star.pivot.system.domain.dto.DictTypeDTO;
import com.star.pivot.system.domain.dto.DictTypeQueryDTO;
import com.star.pivot.system.service.DictTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型管理控制器
 *
 * @author stardust
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/sys/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @PreAuthorize("hasAuthority('system:type:query')")
    @PostMapping("/list")
    public Result<PageResponse<DictTypeVO>> list(@RequestBody DictTypeQueryDTO queryDTO) {
        PageResponse<DictTypeVO> page = dictTypeService.selectDictTypePage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据字典类型ID查询详情
     */
    @PreAuthorize("hasAuthority('system:type:query')")
    @GetMapping("/{dictId}")
    public Result<DictTypeVO> getInfo(@PathVariable Long dictId) {
        DictTypeVO dictTypeVO = dictTypeService.selectDictTypeById(dictId);
        return Result.success(dictTypeVO);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("hasAuthority('system:type:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody DictTypeDTO dictTypeDTO) {
        boolean success = dictTypeService.insertDictType(dictTypeDTO);
        return success ? Result.success("新增字典类型成功") : Result.error("新增字典类型失败");
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("hasAuthority('system:type:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DictTypeDTO dictTypeDTO) {
        boolean success = dictTypeService.updateDictType(dictTypeDTO);
        return success ? Result.success("修改字典类型成功") : Result.error("修改字典类型失败");
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("hasAuthority('system:type:delete')")
    @DeleteMapping("/{dictIds}")
    public Result<?> remove(@PathVariable Long[] dictIds) {
        boolean success = dictTypeService.deleteDictTypeByIds(dictIds);
        return success ? Result.success("删除字典类型成功") : Result.error("删除字典类型失败");
    }
}

