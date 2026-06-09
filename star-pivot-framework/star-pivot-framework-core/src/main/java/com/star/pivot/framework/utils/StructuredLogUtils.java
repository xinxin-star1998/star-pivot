package com.star.pivot.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 结构化日志工具类
 * 提供结构化日志记录和MDC上下文管理功能
 */
@Slf4j
public class StructuredLogUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static final String TRACE_ID = "traceId";
    public static final String REQUEST_ID = "requestId";
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String OPERATION = "operation";
    public static final String MODULE = "module";

    /**
     * 开始一个新的跟踪上下文
     * @return 生成的traceId
     */
    public static String startTrace() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    /**
     * 设置请求ID
     * @param requestId 请求ID
     */
    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }

    /**
     * 设置用户信息
     * @param userId 用户ID
     * @param username 用户名
     */
    public static void setUserInfo(Long userId, String username) {
        if (userId != null) {
            MDC.put(USER_ID, String.valueOf(userId));
        }
        if (username != null) {
            MDC.put(USERNAME, username);
        }
    }

    /**
     * 设置操作和模块信息
     * @param operation 操作
     * @param module 模块
     */
    public static void setOperation(String operation, String module) {
        if (operation != null) {
            MDC.put(OPERATION, operation);
        }
        if (module != null) {
            MDC.put(MODULE, module);
        }
    }

    /**
     * 记录操作日志
     * @param operation 操作
     * @param module 模块
     * @param params 参数
     * @param result 结果
     * @param success 是否成功
     */
    public static void logOperation(String operation, String module, Map<String, Object> params, Object result, boolean success) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("event", "operation");
            logMap.put("operation", operation);
            logMap.put("module", module);
            logMap.put("success", success);
            logMap.put("timestamp", System.currentTimeMillis());

            if (params != null && !params.isEmpty()) {
                logMap.put("params", desensitizeParams(params));
            }

            if (result != null) {
                logMap.put("result", result);
            }

            String logJson = objectMapper.writeValueAsString(logMap);
            if (success) {
                log.info(logJson);
            } else {
                log.warn(logJson);
            }
        } catch (Exception e) {
            log.warn("记录操作日志失败", e);
        }
    }

    /**
     * 记录业务事件日志
     * @param event 事件类型
     * @param message 消息
     * @param data 数据
     */
    public static void logBusinessEvent(String event, String message, Map<String, Object> data) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("event", event);
            logMap.put("message", message);
            logMap.put("timestamp", System.currentTimeMillis());

            if (data != null && !data.isEmpty()) {
                logMap.put("data", data);
            }

            String logJson = objectMapper.writeValueAsString(logMap);
            log.info(logJson);
        } catch (Exception e) {
            log.warn("记录业务事件日志失败", e);
        }
    }

    /**
     * 记录性能指标
     * @param operation 操作
     * @param duration 耗时(ms)
     * @param success 是否成功
     */
    public static void logPerformance(String operation, long duration, boolean success) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("event", "performance");
            logMap.put("operation", operation);
            logMap.put("duration", duration);
            logMap.put("success", success);
            logMap.put("unit", "milliseconds");
            logMap.put("timestamp", System.currentTimeMillis());

            String logJson = objectMapper.writeValueAsString(logMap);
            log.info(logJson);
        } catch (Exception e) {
            log.warn("记录性能日志失败", e);
        }
    }

    /**
     * 脱敏参数
     * @param params 原始参数
     * @return 脱敏后的参数
     */
    private static Map<String, Object> desensitizeParams(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (isSensitiveField(key)) {
                result.put(key, "***");
            } else if (value instanceof String && isSensitiveContent((String) value)) {
                result.put(key, "***");
            } else {
                result.put(key, value);
            }
        }

        return result;
    }

    /**
     * 判断是否为敏感字段
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    private static boolean isSensitiveField(String fieldName) {
        if (fieldName == null) {
            return false;
        }

        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") ||
               lowerFieldName.contains("pwd") ||
               lowerFieldName.contains("token") ||
               lowerFieldName.contains("secret") ||
               lowerFieldName.contains("key") ||
               lowerFieldName.contains("credential") ||
               lowerFieldName.contains("idcard") ||
               lowerFieldName.contains("phone") ||
               lowerFieldName.contains("email") ||
               lowerFieldName.contains("card") ||
               lowerFieldName.contains("ssn") ||
               lowerFieldName.contains("cvv") ||
               lowerFieldName.contains("cvc");
    }

    /**
     * 判断是否为敏感内容（如手机号、身份证等）
     * @param content 内容
     * @return 是否为敏感内容
     */
    private static boolean isSensitiveContent(String content) {
        if (content == null || content.length() < 5) {
            return false;
        }

        // 匹配手机号模式
        if (content.matches("\\d{11}")) {
            return true;
        }

        // 匹配身份证号模式
        if (content.matches("\\d{17}[\\dXx]")) {
            return true;
        }

        // 匹配邮箱模式
        if (content.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            return true;
        }

        // 匹配银行卡号模式
        if (content.matches("\\d{16,19}")) {
            return true;
        }

        return false;
    }

    /**
     * 清理MDC上下文
     */
    public static void clearContext() {
        MDC.clear();
    }
}