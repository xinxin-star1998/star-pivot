package com.star.pivot.framework.exception;

import com.star.pivot.framework.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 统一异常处理工具类
 * 提供更友好的错误信息和结构化异常日志
 */
@Slf4j
public class EnhancedExceptionProcessor {

    /**
     * 处理并记录业务异常
     * @param exception 业务异常
     * @param operation 操作名称
     * @param module 模块名称
     * @return 错误结果
     */
    public static Result<Void> processBizException(BizException exception, String operation, String module) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = exception.getDisplayMessage();

        logBusinessException(errorCode, message, operation, module, exception);

        // 返回用户友好的错误信息
        return Result.error(errorCode.getCode(), formatFriendlyMessage(message, errorCode));
    }

    /**
     * 处理并记录系统异常
     * @param exception 系统异常
     * @param operation 操作名称
     * @param module 模块名称
     * @return 错误结果
     */
    public static Result<Void> processSystemException(Exception exception, String operation, String module) {
        String traceId = MDC.get("traceId"); // 获取当前追踪ID

        logSystemException(exception, operation, module, traceId);

        // 根据异常类型返回不同的友好错误信息
        String friendlyMessage = classifyAndFormatException(exception);

        return Result.error(ErrorCode.INTERNAL_ERROR.getCode(), friendlyMessage);
    }

    /**
     * 处理并记录验证异常
     * @param fieldErrors 字段错误信息
     * @param operation 操作名称
     * @param module 模块名称
     * @return 错误结果
     */
    public static Result<Void> processValidationException(Map<String, String> fieldErrors, String operation, String module) {
        String message = formatValidationMessage(fieldErrors);

        logValidationError(message, operation, module, fieldErrors);

        return Result.error(ErrorCode.VALIDATE_ERROR.getCode(), message);
    }

    /**
     * 记录业务异常
     */
    private static void logBusinessException(ErrorCode errorCode, String message, String operation, String module, Exception exception) {
        try {
            // 构造结构化日志信息
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("{");
            logBuilder.append("\"event\":\"business_exception\",");
            logBuilder.append("\"error_code\":").append(errorCode.getCode()).append(",");
            logBuilder.append("\"error_message\":\"").append(escapeJson(message)).append("\",");
            logBuilder.append("\"operation\":\"").append(escapeJson(operation)).append("\",");
            logBuilder.append("\"module\":\"").append(escapeJson(module)).append("\",");
            logBuilder.append("\"timestamp\":").append(System.currentTimeMillis());

            String traceId = MDC.get("traceId");
            if (traceId != null) {
                logBuilder.append(",\"trace_id\":\"").append(traceId).append("\"");
            }

            logBuilder.append("}");

            log.warn(logBuilder.toString());
        } catch (Exception e) {
            log.warn("记录业务异常日志失败", e);
        }
    }

    /**
     * 记录系统异常
     */
    private static void logSystemException(Exception exception, String operation, String module, String traceId) {
        try {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("{");
            logBuilder.append("\"event\":\"system_exception\",");
            logBuilder.append("\"exception_type\":\"").append(exception.getClass().getSimpleName()).append("\",");
            logBuilder.append("\"message\":\"").append(escapeJson(exception.getMessage())).append("\",");
            logBuilder.append("\"operation\":\"").append(escapeJson(operation)).append("\",");
            logBuilder.append("\"module\":\"").append(escapeJson(module)).append("\",");
            logBuilder.append("\"timestamp\":").append(System.currentTimeMillis());

            if (traceId != null) {
                logBuilder.append(",\"trace_id\":\"").append(traceId).append("\"");
            }

            logBuilder.append("}");

            log.error(logBuilder.toString(), exception);
        } catch (Exception e) {
            log.error("记录系统异常日志失败", e);
        }
    }

    /**
     * 记录验证错误
     */
    private static void logValidationError(String message, String operation, String module, Map<String, String> fieldErrors) {
        try {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("{");
            logBuilder.append("\"event\":\"validation_error\",");
            logBuilder.append("\"message\":\"").append(escapeJson(message)).append("\",");
            logBuilder.append("\"operation\":\"").append(escapeJson(operation)).append("\",");
            logBuilder.append("\"module\":\"").append(escapeJson(module)).append("\",");
            logBuilder.append("\"timestamp\":").append(System.currentTimeMillis());

            if (fieldErrors != null && !fieldErrors.isEmpty()) {
                logBuilder.append(",\"field_errors\":{");
                boolean first = true;
                for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
                    if (!first) logBuilder.append(",");
                    logBuilder.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                             .append(escapeJson(entry.getValue())).append("\"");
                    first = false;
                }
                logBuilder.append("}");
            }

            String traceId = MDC.get("traceId");
            if (traceId != null) {
                logBuilder.append(",\"trace_id\":\"").append(traceId).append("\"");
            }

            logBuilder.append("}");

            log.warn(logBuilder.toString());
        } catch (Exception e) {
            log.warn("记录验证错误日志失败", e);
        }
    }

    /**
     * 格式化友好的错误消息
     */
    private static String formatFriendlyMessage(String originalMessage, ErrorCode errorCode) {
        // 根据错误代码返回更友好的消息
        switch (errorCode) {
            case UNAUTHORIZED:
                return "登录凭证已过期，请重新登录";
            case FORBIDDEN:
                return "权限不足，无法执行此操作";
            case NOT_FOUND:
                return "请求的资源不存在";
            case VALIDATE_ERROR:
                return "输入参数有误，请检查后重试";
            case PARAM_INVALID:
                return "参数格式不正确，请检查后重试";
            case PARAM_NOT_NULL:
                return "缺少必填参数，请补充后重试";
            default:
                return originalMessage != null ? originalMessage : "操作失败，请稍后重试";
        }
    }

    /**
     * 根据异常类型分类并格式化错误消息
     */
    private static String classifyAndFormatException(Exception exception) {
        if (exception instanceof NullPointerException) {
            return "系统繁忙，请稍后重试";
        } else if (exception instanceof IllegalArgumentException) {
            return "请求参数不合法";
        } else if (exception instanceof ClassCastException) {
            return "数据类型错误";
        } else if (exception instanceof java.util.concurrent.TimeoutException) {
            return "请求超时，请稍后重试";
        } else if (isDataAccessException(exception)) {
            return "数据操作失败，请稍后重试";
        } else if (exception instanceof java.net.ConnectException) {
            return "网络连接失败，请检查网络后重试";
        } else {
            return "系统异常，请稍后重试";
        }
    }

    /**
     * 格式化验证错误消息
     */
    private static String formatValidationMessage(Map<String, String> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            return "输入参数验证失败";
        }

        StringBuilder message = new StringBuilder("以下字段验证失败：");
        boolean first = true;
        for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
            if (!first) {
                message.append("；");
            }
            message.append(entry.getKey()).append("：").append(entry.getValue());
            first = false;
        }

        return message.toString();
    }

    /**
     * 判断是否为数据访问异常（通过类名判断，避免直接依赖 spring-jdbc）
     */
    private static boolean isDataAccessException(Exception exception) {
        Class<?> clazz = exception.getClass();
        while (clazz != null) {
            if ("org.springframework.dao.DataAccessException".equals(clazz.getName())) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * 转义JSON字符串
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}