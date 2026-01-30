package com.star.pivot.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 将字符串转换为 LocalDateTime
 * 支持多种日期时间格式：
 * 1. yyyy-MM-dd HH:mm:ss（常用格式，带空格）
 * 2. yyyy-MM-ddTHH:mm:ss（ISO 格式）
 * 3. yyyy-MM-dd HH:mm:ss.SSS（带毫秒）
 * 4. yyyy-MM-ddTHH:mm:ss.SSS（ISO 格式带毫秒）
 *
 * @author StarPivot
 */
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    /**
     * 常用日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter COMMON_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * ISO 日期时间格式：yyyy-MM-ddTHH:mm:ss
     */
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 带毫秒的常用格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    private static final DateTimeFormatter COMMON_MILLIS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 带毫秒的 ISO 格式：yyyy-MM-ddTHH:mm:ss.SSS
     */
    private static final DateTimeFormatter ISO_MILLIS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public LocalDateTime convert(@NonNull String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String trimmed = source.trim();

        // 按优先级尝试不同的格式
        try {
            // 1. 尝试常用格式：yyyy-MM-dd HH:mm:ss
            return LocalDateTime.parse(trimmed, COMMON_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                // 2. 尝试 ISO 格式：yyyy-MM-ddTHH:mm:ss
                return LocalDateTime.parse(trimmed, ISO_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    // 3. 尝试带毫秒的常用格式：yyyy-MM-dd HH:mm:ss.SSS
                    return LocalDateTime.parse(trimmed, COMMON_MILLIS_FORMATTER);
                } catch (DateTimeParseException e3) {
                    try {
                        // 4. 尝试带毫秒的 ISO 格式：yyyy-MM-ddTHH:mm:ss.SSS
                        return LocalDateTime.parse(trimmed, ISO_MILLIS_FORMATTER);
                    } catch (DateTimeParseException e4) {
                        // 所有格式都失败，抛出异常
                        throw new IllegalArgumentException(
                                String.format("无法解析日期时间字符串 '%s'，支持的格式：yyyy-MM-dd HH:mm:ss、yyyy-MM-ddTHH:mm:ss、yyyy-MM-dd HH:mm:ss.SSS、yyyy-MM-ddTHH:mm:ss.SSS", trimmed),
                                e4);
                    }
                }
            }
        }
    }
}
