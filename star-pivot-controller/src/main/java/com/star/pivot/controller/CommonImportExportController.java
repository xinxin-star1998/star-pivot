package com.star.pivot.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.star.pivot.common.annotation.Log;
import com.star.pivot.common.annotation.NoResponseWrapper;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.service.ImportExportService;
import com.star.pivot.system.service.ImportExportServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
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
     * 直接写入 HttpServletResponse 流，不加统一 Result 包装，故排除 ResponseBodyAdvice
     * </p>
     * <p>
     * 权限要求：需要拥有对应业务模块的导出权限，如 `system:user:export`、`system:dept:export` 等
     * </p>
     *
     * @param businessType 业务类型标识
     * @param queryParams  查询参数
     * @param response     HTTP 响应
     */
    @Log(title = "数据导出", businessType = 0)
    @PreAuthorize("hasAuthority('system:' + #businessType + ':export')")
    @NoResponseWrapper
    @PostMapping("/export/{businessType}")
    public void exportData(
            @PathVariable("businessType") String businessType,
            @RequestBody(required = false) Map<String, Object> queryParams,
            HttpServletResponse response) throws IOException {

        try {
            ImportExportService service = serviceFactory.getService(businessType);
            List<Map<String, Object>> dataList = service.exportData(queryParams != null ? queryParams : Map.of());

            generateExcelFile(dataList, businessType, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"导出失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 下载导入模板
     * <p>
     * 直接写入 HttpServletResponse 流，不加统一 Result 包装，故排除 ResponseBodyAdvice
     * </p>
     * <p>
     * 权限要求：需要拥有对应业务模块的导入权限（下载模板是为了导入），如 `system:user:import`、`system:dept:import` 等
     * </p>
     *
     * @param businessType 业务类型标识
     * @param response     HTTP 响应
     */
    @Log(title = "下载导入模板", businessType = 0)
    @PreAuthorize("hasAuthority('system:' + #businessType + ':import')")
    @NoResponseWrapper
    @GetMapping("/template/{businessType}")
    public void downloadTemplate(
            @PathVariable("businessType") String businessType,
            HttpServletResponse response) throws IOException {

        try {
            ImportExportService service = serviceFactory.getService(businessType);
            ImportExportService.ImportTemplate template = service.getImportTemplate();

            generateTemplateExcelFile(template, businessType, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"下载模板失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 使用 EasyExcel 生成导出文件并写入响应流
     *
     * @param dataList     数据列表（Map 的 key 为表头，value 为单元格值）
     * @param businessType 业务类型
     * @param response     HTTP 响应
     */
    private void generateExcelFile(List<Map<String, Object>> dataList, String businessType, HttpServletResponse response)
            throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"msg\":\"没有可导出的数据\"}");
            return;
        }

        // 表头：取第一行 key，保证顺序（若为 LinkedHashMap）
        Map<String, Object> firstRow = dataList.get(0);
        List<String> headers = new ArrayList<>(firstRow.keySet());

        // EasyExcel 动态表头：每列一个 List<String>
        List<List<String>> headList = new ArrayList<>();
        for (String h : headers) {
            List<String> col = new ArrayList<>();
            col.add(h);
            headList.add(col);
        }

        // 动态数据：每行一个 List<Object>，顺序与表头一致
        List<List<Object>> dataRows = new ArrayList<>();
        for (Map<String, Object> rowMap : dataList) {
            List<Object> row = new ArrayList<>();
            for (String key : headers) {
                Object val = rowMap.get(key);
                row.add(val != null ? val : "");
            }
            dataRows.add(row);
        }

        String filename = businessType + "_export_" + System.currentTimeMillis() + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        try (OutputStream out = response.getOutputStream()) {
            EasyExcel.write(out)
                    .head(headList)
                    .sheet("数据导出")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dataRows);
            out.flush();
        }
    }

    /**
     * 使用 EasyExcel 生成导入模板并写入响应流
     *
     * @param template     模板（表头 + 示例行）
     * @param businessType 业务类型
     * @param response     HTTP 响应
     */
    private void generateTemplateExcelFile(ImportExportService.ImportTemplate template, String businessType,
                                           HttpServletResponse response) throws IOException {
        List<String> headers = template.getHeaders();
        if (headers == null || headers.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"msg\":\"模板数据为空\"}");
            return;
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

        String filename = businessType + "_import_template.xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        try (OutputStream out = response.getOutputStream()) {
            EasyExcel.write(out)
                    .head(headList)
                    .sheet("导入模板")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dataRows);
            out.flush();
        }
    }
}
