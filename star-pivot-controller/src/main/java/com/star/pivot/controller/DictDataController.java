package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.DictDataVO;
import com.star.pivot.system.domain.dto.DictDataDTO;
import com.star.pivot.system.domain.dto.DictDataQueryDTO;
import com.star.pivot.system.service.DictDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @PostMapping("/list")
    public Result<PageResponse<DictDataVO>> list(@RequestBody DictDataQueryDTO queryDTO) {
        PageResponse<DictDataVO> page = dictDataService.selectDictDataPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @GetMapping("/type/{dictType}")
    public Result<List<DictDataVO>> getDataByType(@PathVariable String dictType) {
        List<DictDataVO> list = dictDataService.selectDictDataByType(dictType);
        return Result.success(list);
    }

    /**
     * 根据字典编码查询字典数据详情
     */
    @GetMapping("/{dictCode}")
    public Result<DictDataVO> getInfo(@PathVariable Long dictCode) {
        DictDataVO dictDataVO = dictDataService.selectDictDataById(dictCode);
        return Result.success(dictDataVO);
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody DictDataDTO dictDataDTO) {
        boolean success = dictDataService.insertDictData(dictDataDTO);
        return success ? Result.success("新增字典数据成功") : Result.error("新增字典数据失败");
    }

    /**
     * 修改字典数据
     */
    @PutMapping
    public Result<?> edit(@Valid @RequestBody DictDataDTO dictDataDTO) {
        boolean success = dictDataService.updateDictData(dictDataDTO);
        return success ? Result.success("修改字典数据成功") : Result.error("修改字典数据失败");
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{dictCodes}")
    public Result<?> remove(@PathVariable Long[] dictCodes) {
        boolean success = dictDataService.deleteDictDataByIds(dictCodes);
        return success ? Result.success("删除字典数据成功") : Result.error("删除字典数据失败");
    }
}

