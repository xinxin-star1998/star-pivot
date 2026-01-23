package com.star.pivot.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志工具类
 * 用于参数脱敏、IP获取等
 *
 * @author xinxin
 * @since 2026-01-23
 */
public class LogUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * User-Agent解析器（单例模式，提高性能）
     */
    private static final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    /**
     * 需要脱敏的字段名（不区分大小写）
     */
    private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
            "password", "pwd", "passwd", "pass",
            "oldPassword", "newPassword", "confirmPassword",
            "token", "accessToken", "refreshToken",
            "secret", "secretKey", "apiKey",
            "idCard", "idcard", "cardNo",
            "phone", "phonenumber", "mobile", "tel",
            "email", "mail",
            "bankCard", "bankcard", "cardNumber",
            "cvv", "cvc"
    );

    /**
     * 手机号脱敏正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");

    /**
     * 邮箱脱敏正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\\w{1,3})(\\w*)(@\\w+\\.\\w+)");

    /**
     * 身份证脱敏正则
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\d{4})");

    /**
     * 银行卡脱敏正则
     */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(\\d{4})\\d+(\\d{4})");

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个IP才是真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            } else {
                return ip.trim();
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 参数脱敏处理
     *
     * @param param 原始参数
     * @return 脱敏后的参数
     */
    public static String desensitizeParam(String param) {
        if (!StringUtils.hasText(param)) {
            return param;
        }

        try {
            // 尝试解析为JSON对象进行字段级脱敏
            Object obj = objectMapper.readValue(param, Object.class);
            String jsonStr = objectMapper.writeValueAsString(obj);
            return desensitizeJson(jsonStr);
        } catch (JsonProcessingException e) {
            // 如果不是JSON格式，直接进行字符串脱敏
            return desensitizeString(param);
        }
    }

    /**
     * JSON字符串脱敏
     *
     * @param jsonStr JSON字符串
     * @return 脱敏后的JSON字符串
     */
    private static String desensitizeJson(String jsonStr) {
        if (!StringUtils.hasText(jsonStr)) {
            return jsonStr;
        }

        String result = jsonStr;
        // 对敏感字段进行脱敏
        for (String field : SENSITIVE_FIELDS) {
            // 匹配字段名（不区分大小写），使用 Pattern 和 Matcher
            String patternStr = "\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]+)\"";
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(result);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String value = matcher.group(1);
                String desensitizedValue = desensitizeValue(field, value);
                matcher.appendReplacement(sb, "\"" + field + "\":\"" + 
                    Matcher.quoteReplacement(desensitizedValue) + "\"");
            }
            matcher.appendTail(sb);
            result = sb.toString();
        }
        return result;
    }

    /**
     * 字符串脱敏（针对非JSON格式）
     *
     * @param str 原始字符串
     * @return 脱敏后的字符串
     */
    private static String desensitizeString(String str) {
        // 手机号脱敏
        str = PHONE_PATTERN.matcher(str).replaceAll("$1****$2");
        // 邮箱脱敏
        str = EMAIL_PATTERN.matcher(str).replaceAll("$1****$3");
        // 身份证脱敏
        str = ID_CARD_PATTERN.matcher(str).replaceAll("$1********$2");
        // 银行卡脱敏
        str = BANK_CARD_PATTERN.matcher(str).replaceAll("$1****$2");
        return str;
    }

    /**
     * 根据字段名和值进行脱敏
     *
     * @param fieldName 字段名
     * @param value     字段值
     * @return 脱敏后的值
     */
    private static String desensitizeValue(String fieldName, String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        String lowerFieldName = fieldName.toLowerCase();
        int length = value.length();

        // 密码类字段：全部替换为*
        if (lowerFieldName.contains("password") || lowerFieldName.contains("pwd") || 
            lowerFieldName.contains("pass") || lowerFieldName.contains("token") ||
            lowerFieldName.contains("secret") || lowerFieldName.contains("key")) {
            return "******";
        }

        // 手机号脱敏
        if (lowerFieldName.contains("phone") || lowerFieldName.contains("mobile") || 
            lowerFieldName.contains("tel")) {
            if (length >= 7) {
                return value.substring(0, 3) + "****" + value.substring(length - 4);
            }
            return "******";
        }

        // 邮箱脱敏
        if (lowerFieldName.contains("email") || lowerFieldName.contains("mail")) {
            int atIndex = value.indexOf('@');
            if (atIndex > 0) {
                String prefix = value.substring(0, Math.min(3, atIndex));
                return prefix + "****" + value.substring(atIndex);
            }
            return "******";
        }

        // 身份证脱敏
        if (lowerFieldName.contains("idcard") || lowerFieldName.contains("cardno")) {
            if (length >= 14) {
                return value.substring(0, 6) + "********" + value.substring(length - 4);
            }
            return "******";
        }

        // 银行卡脱敏
        if (lowerFieldName.contains("bankcard") || lowerFieldName.contains("cardnumber")) {
            if (length >= 8) {
                return value.substring(0, 4) + "****" + value.substring(length - 4);
            }
            return "******";
        }

        // 默认脱敏：保留前后各2位
        if (length > 4) {
            return value.substring(0, 2) + "****" + value.substring(length - 2);
        } else if (length > 2) {
            return value.substring(0, 1) + "****";
        } else {
            return "****";
        }
    }

    /**
     * 将对象转换为JSON字符串（用于记录响应结果）
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            String json = objectMapper.writeValueAsString(obj);
            // 限制长度，避免过长（考虑UTF-8多字节字符）
            return truncateString(json, 2000);
        } catch (JsonProcessingException e) {
            String str = obj.toString();
            return truncateString(str, 2000);
        }
    }

    /**
     * 截断字符串，确保不超过指定长度（考虑UTF-8多字节字符）
     * 
     * @param str 原始字符串
     * @param maxLength 最大长度（字符数，varchar长度）
     * @return 截断后的字符串
     */
    public static String truncateString(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        // 如果字符串长度（字符数）已经小于等于最大长度，检查字节长度
        if (str.length() <= maxLength) {
            byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            // 如果字节长度也在限制内，直接返回
            if (bytes.length <= maxLength) {
                return str;
            }
        }
        
        // 需要截断，逐步减少字符直到字节长度符合要求
        // 预留一些空间给省略号（"..." 占3个字符，但UTF-8编码可能占用更多字节）
        int targetLength = Math.min(str.length(), maxLength - 10);
        if (targetLength <= 0) {
            return "...";
        }
        
        String truncated = str.substring(0, targetLength);
        byte[] bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // 如果字节长度仍然超过，继续截断
        while (bytes.length > maxLength - 10 && targetLength > 0) {
            targetLength--;
            if (targetLength <= 0) {
                return "...";
            }
            truncated = str.substring(0, targetLength);
            bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        
        return truncated + "...";
    }

    /**
     * 获取浏览器类型
     *
     * @param request HTTP请求
     * @return 浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        try {
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtils.hasText(userAgent)) {
                return "";
            }
            UserAgent agent = userAgentAnalyzer.parse(userAgent);
            String browserName = agent.getValue("AgentName");
            String browserVersion = agent.getValue("AgentVersion");
            
            // 过滤无效的浏览器名称和版本号
            if (StringUtils.hasText(browserName) && !browserName.equals("??") && !browserName.equals("Unknown")) {
                // 过滤无效的版本号（如 "??", "Unknown", 空字符串等）
                if (StringUtils.hasText(browserVersion) && 
                    !browserVersion.equals("??") && 
                    !browserVersion.equals("Unknown") &&
                    !browserVersion.trim().isEmpty()) {
                    return truncateString(browserName + " " + browserVersion, 50);
                }
                return truncateString(browserName, 50);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取操作系统
     *
     * @param request HTTP请求
     * @return 操作系统
     */
    public static String getOs(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        try {
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtils.hasText(userAgent)) {
                return "";
            }
            UserAgent agent = userAgentAnalyzer.parse(userAgent);
            String osName = agent.getValue("OperatingSystemName");
            String osVersion = agent.getValue("OperatingSystemVersion");
            
            // 对于Windows系统，尝试获取更详细的版本信息
            if (osName != null && osName.contains("Windows")) {
                // 尝试从User-Agent字符串中提取Windows版本
                String winVersion = extractWindowsVersion(userAgent);
                if (StringUtils.hasText(winVersion)) {
                    return truncateString("Windows " + winVersion, 50);
                }
                // 如果无法提取，使用优化后的名称
                return truncateString(optimizeOsName(osName), 50);
            }
            
            // 过滤无效的操作系统名称和版本号
            if (StringUtils.hasText(osName) && !osName.equals("??") && !osName.equals("Unknown")) {
                // 过滤无效的版本号（如 "??", "Unknown", 空字符串等）
                if (StringUtils.hasText(osVersion) && 
                    !osVersion.equals("??") && 
                    !osVersion.equals("Unknown") &&
                    !osVersion.trim().isEmpty()) {
                    // 清理版本号中的 "??" 字符
                    String cleanVersion = osVersion.replace("??", "").trim();
                    if (StringUtils.hasText(cleanVersion)) {
                        return truncateString(osName + " " + cleanVersion, 50);
                    }
                }
                // 如果版本号无效，只返回操作系统名称
                // 优化常见的操作系统名称显示
                String cleanOsName = optimizeOsName(osName);
                return truncateString(cleanOsName, 50);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * 从User-Agent字符串中提取Windows版本
     *
     * @param userAgent User-Agent字符串
     * @return Windows版本，如果无法提取则返回null
     */
    private static String extractWindowsVersion(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return null;
        }
        
        // Windows 11
        if (userAgent.contains("Windows NT 10.0") && userAgent.contains("Win64; x64")) {
            // 检查是否是Windows 11（通过特定标识）
            if (userAgent.contains("Edg") || userAgent.contains("Chrome")) {
                // Windows 11的User-Agent通常包含Windows NT 10.0
                // 可以通过其他方式判断，这里简化处理
                return "10/11";
            }
        }
        
        // Windows 10
        if (userAgent.contains("Windows NT 10.0")) {
            return "10";
        }
        
        // Windows 8.1
        if (userAgent.contains("Windows NT 6.3")) {
            return "8.1";
        }
        
        // Windows 8
        if (userAgent.contains("Windows NT 6.2")) {
            return "8";
        }
        
        // Windows 7
        if (userAgent.contains("Windows NT 6.1")) {
            return "7";
        }
        
        // Windows Vista
        if (userAgent.contains("Windows NT 6.0")) {
            return "Vista";
        }
        
        // Windows XP
        if (userAgent.contains("Windows NT 5.1")) {
            return "XP";
        }
        
        return null;
    }
    
    /**
     * 优化操作系统名称显示
     *
     * @param osName 原始操作系统名称
     * @return 优化后的操作系统名称
     */
    private static String optimizeOsName(String osName) {
        if (!StringUtils.hasText(osName)) {
            return osName;
        }
        
        // 处理常见的操作系统名称
        String name = osName.trim();
        
        // Windows NT 系列优化
        if (name.startsWith("Windows NT")) {
            // 尝试从User-Agent中提取更具体的Windows版本
            // 这里简化处理，直接返回 "Windows"
            return "Windows";
        }
        
        // 其他常见操作系统优化
        if (name.contains("Mac OS")) {
            return "macOS";
        }
        
        if (name.contains("Linux")) {
            return "Linux";
        }
        
        if (name.contains("Android")) {
            return "Android";
        }
        
        if (name.contains("iOS")) {
            return "iOS";
        }
        
        return name;
    }

    /**
     * 根据IP地址获取登录地点（简化版，实际项目中可以使用IP地址库）
     *
     * @param ip IP地址
     * @return 登录地点
     */
    public static String getLoginLocation(String ip) {
        if (!StringUtils.hasText(ip)) {
            return "";
        }
        // 内网IP
        if (ip.startsWith("127.") || ip.startsWith("192.168.") || 
            ip.startsWith("10.") || ip.startsWith("172.") || 
            ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "内网IP";
        }
        // 实际项目中可以使用IP地址库（如ip2region、GeoIP2等）来获取详细地址
        // 这里简化处理，返回空字符串，由调用方决定是否使用IP地址库
        return "";
    }
}
