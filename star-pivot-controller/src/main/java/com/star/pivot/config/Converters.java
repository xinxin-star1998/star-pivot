package com.star.pivot.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Web 层通用参数转换器（集中定义，供 WebMvcConfig 注册）
 * <p>支持 @PathVariable / @RequestParam 的字符串到 LocalDate、LocalDateTime、Long[] 的解析
 */
public final class Converters {

    private Converters() {}

    /** 逗号分隔字符串 → Long[]，用于如 "/1,2,3" 批量删除 */
    public static class StringToLongArrayConverter implements Converter<String, Long[]> {
        @Override
        public Long[] convert(@NonNull String source) {
            if (source == null || source.isBlank()) {
                return new Long[0];
            }
            return Arrays.stream(source.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toArray(Long[]::new);
        }
    }

    /** 字符串 → LocalDateTime，支持 yyyy-MM-dd HH:mm:ss、ISO、带毫秒等 */
    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        );
        private static final String SUPPORTED_FORMATS = "yyyy-MM-dd HH:mm:ss、ISO、带毫秒";

        @Override
        public LocalDateTime convert(@NonNull String source) {
            if (source == null || source.isBlank()) return null;
            String s = source.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(s, formatter);
                } catch (DateTimeParseException ignored) {
                    // 尝试下一个格式
                }
            }
            throw new IllegalArgumentException("无法解析日期时间: " + s + "，支持格式: " + SUPPORTED_FORMATS);
        }
    }

    /** 字符串 → LocalDate，支持 yyyy-MM-dd、yyyy/MM/dd */
    public static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        );
        private static final String SUPPORTED_FORMATS = "yyyy-MM-dd、yyyy/MM/dd";

        @Override
        public LocalDate convert(@NonNull String source) {
            if (source == null || source.isBlank()) return null;
            String s = source.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDate.parse(s, formatter);
                } catch (DateTimeParseException ignored) {
                    // 尝试下一个格式
                }
            }
            throw new IllegalArgumentException("无法解析日期: " + s + "，支持格式: " + SUPPORTED_FORMATS);
        }
    }
}
