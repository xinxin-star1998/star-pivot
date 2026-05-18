package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.excel.ExcelToolkit;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigDTO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.excel.SysConfigExcel;
import com.star.pivot.system.excel.SysConfigExcelHandler;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置控制器
 *
 * @author admin
 * @since 2026-03-31
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService sysConfigService;
    private final SysConfigExcelHandler sysConfigExcelHandler;

    /**
     * 分页查询参数配置列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @PreAuthorize("hasAuthority('system:config:query')")
    @PostMapping("/list")
    public Result<PageResponse<SysConfigVO>> list(@RequestBody SysConfigQueryDTO queryDTO) {
        PageResponse<SysConfigVO> page = sysConfigService.selectSysConfigPage(queryDTO);
        return Result.success(page);
    }

    /** EasyExcel 导出参数配置 */
    @Log(title = "导出参数配置", businessType = AppConstants.BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:config:export')")
    @NoResponseWrapper
    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody(required = false) SysConfigQueryDTO queryDTO) {
        SysConfigQueryDTO exportQuery = queryDTO == null ? new SysConfigQueryDTO() : queryDTO;
        return ExcelToolkit.export(sysConfigExcelHandler, exportQuery, SysConfigExcel.class);
    }

    /**
     * 获取参数配置详细信息
     *
     * @param configId 参数配置主键
     * @return 参数配置信息
     */
    @PreAuthorize("hasAuthority('system:config:query')")
    @GetMapping(value = "/{configId}")
    public Result<SysConfigVO> getInfo(@PathVariable("configId") Long configId) {
        SysConfigVO sysConfigVO = sysConfigService.selectSysConfigByConfigId(configId);
        return Result.success(sysConfigVO);
    }

    /**
     * 新增参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 操作结果
     */
    @Log(title = "新增参数配置", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:config:add')")
    @PostMapping
    public Result<?> add(@Valid @RequestBody SysConfigDTO sysConfigDTO) {
        boolean success = sysConfigService.insertSysConfig(sysConfigDTO);
        return success ? Result.success("新增参数配置成功") : Result.error("新增参数配置失败");
    }

    /**
     * 修改参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 操作结果
     */
    @Log(title = "修改参数配置", businessType = AppConstants.BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:config:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody SysConfigDTO sysConfigDTO) {
        boolean success = sysConfigService.updateSysConfig(sysConfigDTO);
        return success ? Result.success("修改参数配置成功") : Result.error("修改参数配置失败");
    }

    /**
     * 删除参数配置（支持批量删除，请求体 ids）
     *
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果
     */
    @Log(title = "删除参数配置", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:config:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> idList = deleteRequest.getIds();
        if (idList == null || idList.isEmpty()) {
            return Result.error("删除ID不能为空");
        }
        Long[] configIds = idList.toArray(new Long[0]);
        boolean success = sysConfigService.deleteSysConfigByConfigIds(configIds);
        return success ? Result.success("删除参数配置成功") : Result.error("删除参数配置失败");
    }
}
