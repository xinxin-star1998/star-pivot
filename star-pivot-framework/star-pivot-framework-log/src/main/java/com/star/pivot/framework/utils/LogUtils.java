package com.star.pivot.framework.utils;

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

public class LogUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

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

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\\w{1,3})(\\w*)(@\\w+\\.\\w+)");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\d{4})");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(\\d{4})\\d+(\\d{4})");

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return "";
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            return index != -1 ? ip.substring(0, index).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) return ip.trim();
        return request.getRemoteAddr();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    public static String desensitizeParam(String param) {
        if (!StringUtils.hasText(param)) return param;
        try {
            Object obj = objectMapper.readValue(param, Object.class);
            return desensitizeJson(objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            return desensitizeString(param);
        }
    }

    private static String desensitizeJson(String jsonStr) {
        if (!StringUtils.hasText(jsonStr)) return jsonStr;
        String result = jsonStr;
        for (String field : SENSITIVE_FIELDS) {
            String patternStr = "\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]+)\"";
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(result);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String value = matcher.group(1);
                String desensitizedValue = desensitizeValue(field, value);
                matcher.appendReplacement(sb, "\"" + field + "\":\"" + Matcher.quoteReplacement(desensitizedValue) + "\"");
            }
            matcher.appendTail(sb);
            result = sb.toString();
        }
        return result;
    }

    private static String desensitizeString(String str) {
        str = PHONE_PATTERN.matcher(str).replaceAll("$1****$2");
        str = EMAIL_PATTERN.matcher(str).replaceAll("$1****$3");
        str = ID_CARD_PATTERN.matcher(str).replaceAll("$1********$2");
        str = BANK_CARD_PATTERN.matcher(str).replaceAll("$1****$2");
        return str;
    }

    private static String desensitizeValue(String fieldName, String value) {
        if (!StringUtils.hasText(value)) return value;
        String lowerFieldName = fieldName.toLowerCase();
        int length = value.length();
        if (lowerFieldName.contains("password") || lowerFieldName.contains("pwd") || lowerFieldName.contains("pass")
                || lowerFieldName.contains("token") || lowerFieldName.contains("secret") || lowerFieldName.contains("key")) {
            return "******";
        }
        if (lowerFieldName.contains("phone") || lowerFieldName.contains("mobile") || lowerFieldName.contains("tel")) {
            return length >= 7 ? value.substring(0, 3) + "****" + value.substring(length - 4) : "******";
        }
        if (lowerFieldName.contains("email") || lowerFieldName.contains("mail")) {
            int atIndex = value.indexOf('@');
            return atIndex > 0 ? value.substring(0, Math.min(3, atIndex)) + "****" + value.substring(atIndex) : "******";
        }
        if (lowerFieldName.contains("idcard") || lowerFieldName.contains("cardno")) {
            return length >= 14 ? value.substring(0, 6) + "********" + value.substring(length - 4) : "******";
        }
        if (lowerFieldName.contains("bankcard") || lowerFieldName.contains("cardnumber")) {
            return length >= 8 ? value.substring(0, 4) + "****" + value.substring(length - 4) : "******";
        }
        if (length > 4) return value.substring(0, 2) + "****" + value.substring(length - 2);
        if (length > 2) return value.substring(0, 1) + "****";
        return "****";
    }

    public static String toJsonString(Object obj) {
        if (obj == null) return "";
        try {
            return truncateString(objectMapper.writeValueAsString(obj), 2000);
        } catch (JsonProcessingException e) {
            return truncateString(obj.toString(), 2000);
        }
    }

    public static String truncateString(String str, int maxLength) {
        if (str == null || str.isEmpty()) return str;
        if (str.length() <= maxLength) {
            if (str.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= maxLength) return str;
        }
        int targetLength = Math.min(str.length(), maxLength - 10);
        if (targetLength <= 0) return "...";
        String truncated = str.substring(0, targetLength);
        byte[] bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        while (bytes.length > maxLength - 10 && targetLength > 0) {
            targetLength--;
            if (targetLength <= 0) return "...";
            truncated = str.substring(0, targetLength);
            bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        return truncated + "...";
    }

    public static String getBrowser(HttpServletRequest request) {
        if (request == null) return "";
        try {
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtils.hasText(userAgent)) return "";
            UserAgent agent = userAgentAnalyzer.parse(userAgent);
            String browserName = agent.getValue("AgentName");
            String browserVersion = agent.getValue("AgentVersion");
            if (StringUtils.hasText(browserName) && !browserName.equals("??") && !browserName.equals("Unknown")) {
                if (StringUtils.hasText(browserVersion) && !browserVersion.equals("??") && !browserVersion.equals("Unknown") && !browserVersion.trim().isEmpty()) {
                    return truncateString(browserName + " " + browserVersion, 50);
                }
                return truncateString(browserName, 50);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getOs(HttpServletRequest request) {
        if (request == null) return "";
        try {
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtils.hasText(userAgent)) return "";
            UserAgent agent = userAgentAnalyzer.parse(userAgent);
            String osName = agent.getValue("OperatingSystemName");
            String osVersion = agent.getValue("OperatingSystemVersion");
            if (osName != null && osName.contains("Windows")) {
                String winVersion = extractWindowsVersion(userAgent);
                if (StringUtils.hasText(winVersion)) return truncateString("Windows " + winVersion, 50);
                return truncateString(optimizeOsName(osName), 50);
            }
            if (StringUtils.hasText(osName) && !osName.equals("??") && !osName.equals("Unknown")) {
                if (StringUtils.hasText(osVersion) && !osVersion.equals("??") && !osVersion.equals("Unknown") && !osVersion.trim().isEmpty()) {
                    String cleanVersion = osVersion.replace("??", "").trim();
                    if (StringUtils.hasText(cleanVersion)) return truncateString(osName + " " + cleanVersion, 50);
                }
                return truncateString(optimizeOsName(osName), 50);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String extractWindowsVersion(String userAgent) {
        if (!StringUtils.hasText(userAgent)) return null;
        if (userAgent.contains("Windows NT 10.0")) return "10";
        if (userAgent.contains("Windows NT 6.3")) return "8.1";
        if (userAgent.contains("Windows NT 6.2")) return "8";
        if (userAgent.contains("Windows NT 6.1")) return "7";
        if (userAgent.contains("Windows NT 6.0")) return "Vista";
        if (userAgent.contains("Windows NT 5.1")) return "XP";
        return null;
    }

    private static String optimizeOsName(String osName) {
        if (!StringUtils.hasText(osName)) return osName;
        String name = osName.trim();
        if (name.startsWith("Windows NT")) return "Windows";
        if (name.contains("Mac OS")) return "macOS";
        if (name.contains("Linux")) return "Linux";
        if (name.contains("Android")) return "Android";
        if (name.contains("iOS")) return "iOS";
        return name;
    }

    public static String getLoginLocation(String ip) {
        if (!StringUtils.hasText(ip)) return "";
        if (ip.startsWith("127.") || ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.") || ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "内网IP";
        }
        return "";
    }
}
