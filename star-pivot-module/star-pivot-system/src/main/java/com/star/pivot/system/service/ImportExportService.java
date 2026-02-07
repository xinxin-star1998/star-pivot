package com.star.pivot.system.service;

import java.util.List;
import java.util.Map;

/**
 * 通用导入导出服务接口
 * 各业务模块实现此接口，提供自己的导入导出逻辑
 *
 * @author xinxin
 * @since 2026-01-25
 */
public interface ImportExportService {

    /**
     * 获取业务类型标识
     * 用于区分不同的业务模块，如：user、dept、role 等
     *
     * @return 业务类型标识
     */
    String getBusinessType();

    /**
     * 导入数据
     * 将 Excel 解析后的行数据列表导入到系统中
     *
     * @param rowList Excel 解析后的行数据列表（key 为表头中文名或英文名）
     * @param options 导入选项（如：是否覆盖已存在数据等）
     * @return 导入结果，包含成功数量、失败数量、错误信息等
     */
    ImportExportResult importData(List<Map<String, Object>> rowList, Map<String, Object> options);

    /**
     * 导出数据
     * 根据查询条件导出数据为 Excel
     *
     * @param queryParams 查询参数
     * @return 导出数据列表
     */
    List<Map<String, Object>> exportData(Map<String, Object> queryParams);

    /**
     * 获取导入模板数据
     * 返回模板的表头和数据示例
     *
     * @return 模板数据（包含表头和数据示例）
     */
    ImportTemplate getImportTemplate();

    /**
     * 导入导出结果
     */
    class ImportExportResult {
        /** 成功导入的数量 */
        private int successCount;
        /** 失败的数量 */
        private int failCount;
        /** 错误信息列表 */
        private List<String> errorMessages;

        public ImportExportResult() {
            this.successCount = 0;
            this.failCount = 0;
            this.errorMessages = new java.util.ArrayList<>();
        }

        public ImportExportResult(int successCount, int failCount, List<String> errorMessages) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.errorMessages = errorMessages != null ? errorMessages : new java.util.ArrayList<>();
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailCount() {
            return failCount;
        }

        public void setFailCount(int failCount) {
            this.failCount = failCount;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }

        public void setErrorMessages(List<String> errorMessages) {
            this.errorMessages = errorMessages;
        }

        public void addError(String error) {
            if (this.errorMessages == null) {
                this.errorMessages = new java.util.ArrayList<>();
            }
            this.errorMessages.add(error);
        }
    }

    /**
     * 导入模板
     */
    class ImportTemplate {
        /** 表头列表（按顺序） */
        private List<String> headers;
        /** 示例数据行 */
        private List<Map<String, Object>> sampleData;

        public ImportTemplate() {
        }

        public ImportTemplate(List<String> headers, List<Map<String, Object>> sampleData) {
            this.headers = headers;
            this.sampleData = sampleData;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public List<Map<String, Object>> getSampleData() {
            return sampleData;
        }

        public void setSampleData(List<Map<String, Object>> sampleData) {
            this.sampleData = sampleData;
        }
    }
}
