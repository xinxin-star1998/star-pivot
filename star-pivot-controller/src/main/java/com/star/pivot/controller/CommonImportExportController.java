package com.star.pivot.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.annotation.NoResponseWrapper;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.service.ImportExportService;
import com.star.pivot.system.service.ImportExportServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用导入导出控制器
 * 使用 EasyExcel 实现 Excel 导出与模板下载，支持不同业务模块的导入导出功能
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@RestController
@RequestMapping("/common/import-export")
public class CommonImportExportController {

    @Autowired
    private ImportExportServiceFactory serviceFactory;

    /**
     * 通用导入接口
     * <p>
     * 权限要求：需要拥有对应业务模块的导入权限，如 `system:user:import`、`system:dept:import` 等
     * </p>
     *
     * @param businessType 业务类型标识（如：user、dept、role 等）
     * @param requestBody  请求体，包含 rowList（数据列表）和 options（导入选项）
     * @return 导入结果
     */
    @Log(title = "数据导入", businessType = 1)
    @PreAuthorize("hasAuthority('system:' + #businessType + ':import')")
    @PostMapping("/import/{businessType}")
    public Result<ImportExportService.ImportExportResult> importData(
            @PathVariable("businessType") String businessType,
            @RequestBody Map<String, Object> requestBody) {

        try {
            // 从请求体中提取数据列表和选项
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rowList = (List<Map<String, Object>>) requestBody.get("rowList");
            @SuppressWarnings("unchecked")
            Map<String, Object> options = (Map<String, Object>) requestBody.get("options");

            if (rowList == null) {
                return Result.error("数据列表不能为空");
            }

            ImportExportService service = serviceFactory.getService(businessType);
            ImportExportService.ImportExportResult result = service.importData(rowList, options);

            String message = String.format("导入完成：成功 %d 条，失败 %d 条",
                    result.getSuccessCount(), result.getFailCount());

            if (result.getFailCount() > 0 && result.getErrorMessages() != null && !result.getErrorMessages().isEmpty()) {
                return Result.success(message + "。错误详情：" + String.join("; ", result.getErrorMessages()), result);
            }

            return Result.success(message, result);
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 通用导出接口
     * <p>
     * 使用 ResponseEntity + byte[] 返回 Excel 字节流，避免 getWriter()/getOutputStream 冲突且无需自定义 MessageConverter。
     * 不加统一 Result 包装，故排除 ResponseBodyAdvice。
     * </p>
     * <p>
     * 权限要求：需要拥有对应业务模块的导出权限，如 `system:user:export`、`system:dept:export` 等
     * </p>
     *
     * @param businessType 业务类型标识
     * @param queryParams  查询参数
     * @return 成功时为 Excel 字节流，失败时为 JSON 错误信息
     */
    @Log(title = "数据导出", businessType = 0)
    @PreAuthorize("hasAuthority('system:' + #businessType + ':export')")
    @NoResponseWrapper
    @PostMapping("/export/{businessType}")
    public ResponseEntity<?> exportData(
            @PathVariable("businessType") String businessType,
            @RequestBody(required = false) Map<String, Object> queryParams) {

        try {
            ImportExportService service = serviceFactory.getService(businessType);
            List<Map<String, Object>> dataList = service.exportData(queryParams != null ? queryParams : Map.of());

            if (dataList == null || dataList.isEmpty()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Result.error("没有可导出的数据"));
            }

            List<List<String>> headList = buildHeadList(dataList);
            List<List<Object>> dataRows = buildDataRows(dataList, headList);
            String filename = businessType + "_export_" + System.currentTimeMillis() + ".xlsx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            EasyExcel.write(baos)
                    .head(headList)
                    .sheet("数据导出")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dataRows);
            byte[] bytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        } catch (Exception e) {
            log.error("导出失败", e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Result.error("导出失败：" + e.getMessage()));
        }
    }

    /**
     * 下载导入模板
     * <p>
     * 使用 ResponseEntity + byte[] 返回 Excel 字节流，避免 getWriter()/getOutputStream 冲突且无需自定义 MessageConverter。
     * </p>
     * <p>
     * 权限要求：需要拥有对应业务模块的导入权限（下载模板是为了导入），如 `system:user:import`、`system:dept:import` 等
     * </p>
     *
     * @param businessType 业务类型标识
     * @return 成功时为 Excel 模板字节流，失败时为 JSON 错误信息
     */
    @Log(title = "下载导入模板", businessType = 0)
    @PreAuthorize("hasAuthority('system:' + #businessType + ':import')")
    @NoResponseWrapper
    @GetMapping("/template/{businessType}")
    public ResponseEntity<?> downloadTemplate(@PathVariable("businessType") String businessType) {
        try {
            ImportExportService service = serviceFactory.getService(businessType);
            ImportExportService.ImportTemplate template = service.getImportTemplate();

            List<String> headers = template.getHeaders();
            if (headers == null || headers.isEmpty()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Result.error("模板数据为空"));
            }

            List<List<String>> headList = new ArrayList<>();
            for (String h : headers) {
                List<String> col = new ArrayList<>();
                col.add(h);
                headList.add(col);
            }

            List<List<Object>> dataRows = new ArrayList<>();
            List<Map<String, Object>> sampleData = template.getSampleData();
            if (sampleData != null && !sampleData.isEmpty()) {
                for (Map<String, Object> rowMap : sampleData) {
                    List<Object> row = new ArrayList<>();
                    for (String key : headers) {
                        Object val = rowMap.get(key);
                        row.add(val != null ? val : "");
                    }
                    dataRows.add(row);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            EasyExcel.write(baos)
                    .head(headList)
                    .sheet("导入模板")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dataRows);
            byte[] bytes = baos.toByteArray();

            String filename = businessType + "_import_template.xlsx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            HttpHeaders headersResp = new HttpHeaders();
            headersResp.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            return ResponseEntity.ok()
                    .headers(headersResp)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        } catch (Exception e) {
            log.error("下载模板失败", e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Result.error("下载模板失败：" + e.getMessage()));
        }
    }

    /**
     * 从数据列表构建 EasyExcel 表头（每列一个 List<String>）
     */
    private static List<List<String>> buildHeadList(List<Map<String, Object>> dataList) {
        Map<String, Object> firstRow = dataList.get(0);
        List<String> headers = new ArrayList<>(firstRow.keySet());
        List<List<String>> headList = new ArrayList<>();
        for (String h : headers) {
            List<String> col = new ArrayList<>();
            col.add(h);
            headList.add(col);
        }
        return headList;
    }

    /**
     * 从数据列表按表头顺序构建 EasyExcel 数据行
     */
    private static List<List<Object>> buildDataRows(List<Map<String, Object>> dataList, List<List<String>> headList) {
        List<String> headers = new ArrayList<>();
        for (List<String> col : headList) {
            headers.add(col.get(0));
        }
        List<List<Object>> dataRows = new ArrayList<>();
        for (Map<String, Object> rowMap : dataList) {
            List<Object> row = new ArrayList<>();
            for (String key : headers) {
                Object val = rowMap.get(key);
                row.add(val != null ? val : "");
            }
            dataRows.add(row);
        }
       return dataRows;
    }
}
