package com.star.pivot.dict.controller;

import com.star.pivot.dict.domain.bo.DictTypeVO;
import com.star.pivot.dict.domain.dto.DictTypeDTO;
import com.star.pivot.dict.domain.dto.DictTypeQueryDTO;
import com.star.pivot.dict.service.DictTypeService;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Tag(name = "字典类型管理", description = "字典类型的增删改查等接口")
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @Operation(summary = "分页查询字典类型", description = "根据条件分页查询字典类型列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:type:query')")
    @PostMapping("/list")
    public Result<PageResponse<DictTypeVO>> list(@RequestBody DictTypeQueryDTO queryDTO) {
        PageResponse<DictTypeVO> page = dictTypeService.selectDictTypePage(queryDTO);
        return Result.success(page);
    }

    /**
     * 下拉字典类型列表
     * @return 下拉字典类型列表 list
     */
    @Operation(summary = "查询字典类型下拉列表", description = "获取所有字典类型列表，用于下拉选择")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/selectList")
    public Result<List<DictTypeVO>> selectList() {
        List<DictTypeVO> list = dictTypeService.selectList();
        return Result.success(list);
    }
    /**
     * 根据字典类型ID查询详情
     */
    @Operation(summary = "获取字典类型详情", description = "根据字典类型ID获取详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = DictTypeVO.class))),
            @ApiResponse(responseCode = "404", description = "字典类型不存在")
    })
    @PreAuthorize("hasAuthority('system:type:query')")
    @GetMapping("/{dictId}")
    public Result<DictTypeVO> getInfo(@Parameter(description = "字典类型ID") @PathVariable Long dictId) {
        DictTypeVO dictTypeVO = dictTypeService.selectDictTypeById(dictId);
        return Result.success(dictTypeVO);
    }

    /**
     * 新增字典类型
     */
    @Operation(summary = "新增字典类型", description = "创建新字典类型")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @Log(title = "新增字典类型", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:type:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody DictTypeDTO dictTypeDTO) {
        boolean success = dictTypeService.insertDictType(dictTypeDTO);
        return success ? Result.success("新增字典类型成功") : Result.error("新增字典类型失败");
    }

    /**
     * 修改字典类型
     */
    @Operation(summary = "修改字典类型", description = "更新字典类型信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "字典类型不存在")
    })
    @Log(title = "修改字典类型", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:type:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DictTypeDTO dictTypeDTO) {
        boolean success = dictTypeService.updateDictType(dictTypeDTO);
        return success ? Result.success("修改字典类型成功") : Result.error("修改字典类型失败");
    }

    /**
     * 删除字典类型（支持单删和批量删除）
     */
    @Operation(summary = "删除字典类型", description = "删除字典类型（支持批量删除），如果字典类型下有字典数据则不能删除")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空或存在字典数据")
    })
    @Log(title = "删除字典类型", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:type:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> dictIds = validateIds(deleteRequest.getIds());
        boolean success = dictTypeService.deleteDictTypeByIds(dictIds);
       return success ? Result.success("删除字典类型成功") : Result.error("删除字典类型失败");
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(
                ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
