package com.star.pivot.dict.controller;

import com.star.pivot.dict.domain.bo.DictDataVO;
import com.star.pivot.dict.domain.dto.DictDataDTO;
import com.star.pivot.dict.domain.dto.DictDataQueryDTO;
import com.star.pivot.dict.service.DictDataService;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
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
 * 字典数据管理控制器
 *
 * @author stardust
 */
@Slf4j
@RestController
@RequestMapping("/sys/dict/data")
@RequiredArgsConstructor
@Tag(name = "字典数据管理", description = "字典数据的增删改查等接口")
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @Operation(summary = "分页查询字典数据", description = "根据条件分页查询字典数据列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:data:query')")
    @PostMapping("/list")
    public Result<PageResponse<DictDataVO>> list(@RequestBody DictDataQueryDTO queryDTO) {
        PageResponse<DictDataVO> page = dictDataService.selectDictDataPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Operation(summary = "根据字典类型查询数据", description = "根据字典类型获取该类型下的所有字典数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:data:query')")
    @GetMapping("/type/{dictType}")
    public Result<List<DictDataVO>> getDataByType(@Parameter(description = "字典类型") @PathVariable String dictType) {
        List<DictDataVO> list = dictDataService.selectDictDataByType(dictType);
        return Result.success(list);
    }

    /**
     * 根据字典编码查询字典数据详情
     */
    @Operation(summary = "获取字典数据详情", description = "根据字典编码获取字典数据的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = DictDataVO.class))),
            @ApiResponse(responseCode = "404", description = "字典数据不存在")
    })
    @PreAuthorize("hasAuthority('system:data:query')")
    @GetMapping("/{dictCode}")
    public Result<DictDataVO> getInfo(@Parameter(description = "字典编码") @PathVariable Long dictCode) {
        DictDataVO dictDataVO = dictDataService.selectDictDataById(dictCode);
        return Result.success(dictDataVO);
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据", description = "创建新字典数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PreAuthorize("hasAuthority('system:data:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody DictDataDTO dictDataDTO) {
        boolean success = dictDataService.insertDictData(dictDataDTO);
        return success ? Result.success("新增字典数据成功") : Result.error("新增字典数据失败");
    }

    /**
     * 修改字典数据
     */
    @Operation(summary = "修改字典数据", description = "更新字典数据信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "字典数据不存在")
    })
    @PreAuthorize("hasAuthority('system:data:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DictDataDTO dictDataDTO) {
        boolean success = dictDataService.updateDictData(dictDataDTO);
        return success ? Result.success("修改字典数据成功") : Result.error("修改字典数据失败");
    }

    /**
     * 删除字典数据（支持单删和批量删除）
     */
    @Operation(summary = "删除字典数据", description = "删除字典数据（支持批量删除）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:data:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> dictCodes = validateIds(deleteRequest.getIds());
        boolean success = dictDataService.deleteDictDataByIds(dictCodes);
        return success ? Result.success("删除字典数据成功") : Result.error("删除字典数据失败");
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new com.star.pivot.framework.exception.ServiceException(
                ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}
