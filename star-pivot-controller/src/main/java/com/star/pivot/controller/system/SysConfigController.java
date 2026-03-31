package com.star.pivot.controller.system;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigDTO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 参数配置控制器
 *
 * @author admin
 * @date 2026-03-31
 */
@Slf4j
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {
    private final ISysConfigService sysConfigService;

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

    /**
     * 导出参数配置
     *
     * @param queryDTO 查询参数
     * @return excel 文件字节流
     */
    @Log(title = "参数配置", businessType = 0)
    @PreAuthorize("hasAuthority('system:config:export')")
    @NoResponseWrapper
    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody(required = false) SysConfigQueryDTO queryDTO) {
        try {
            SysConfigQueryDTO exportQuery = queryDTO == null ? new SysConfigQueryDTO() : queryDTO;
            List<SysConfigVO> list = sysConfigService.selectSysConfigList(exportQuery);
            if (list == null || list.isEmpty()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Result.error("没有可导出的数据"));
            }

            List<ConfigExportRow> rows = list.stream()
                    .map(item -> new ConfigExportRow(
                            item.getConfigName(),
                            item.getConfigKey(),
                            item.getConfigValue(),
                            item.getConfigType(),
                            item.getRemark()))
                    .toList();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            EasyExcel.write(baos)
                    .head(ConfigExportRow.class)
                    .autoCloseStream(false)
                    .sheet("参数配置")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(rows);
            byte[] bytes = baos.toByteArray();

            String filename = "sys_config_export_" + System.currentTimeMillis() + ".xlsx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        } catch (Exception e) {
            log.error("导出参数配置失败", e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Result.error("导出失败：" + e.getMessage()));
        }
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

    /**
     * 参数配置导出行定义
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ConfigExportRow {
        @ExcelProperty("参数名称")
        private String configName;

        @ExcelProperty("参数键名")
        private String configKey;

        @ExcelProperty("参数键值")
        private String configValue;

        @ExcelProperty("系统内置")
        private String configType;

        @ExcelProperty("备注")
        private String remark;
    }
}
