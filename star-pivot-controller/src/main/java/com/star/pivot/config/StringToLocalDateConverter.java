package com.star.pivot.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 将字符串转换为 LocalDate
 * 支持多种日期格式：
 * 1. yyyy-MM-dd（常用格式）
 * 2. yyyy/MM/dd（斜杠分隔）
 *
 * @author xinxin
 * @since 2026-01-25
 */
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    /**
     * 常用日期格式：yyyy-MM-dd
     */
    private static final DateTimeFormatter COMMON_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 斜杠分隔格式：yyyy/MM/dd
     */
    private static final DateTimeFormatter SLASH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public LocalDate convert(@NonNull String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String trimmed = source.trim();

        // 按优先级尝试不同的格式
        try {
            // 1. 尝试常用格式：yyyy-MM-dd
            return LocalDate.parse(trimmed, COMMON_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                // 2. 尝试斜杠格式：yyyy/MM/dd
                return LocalDate.parse(trimmed, SLASH_FORMATTER);
            } catch (DateTimeParseException e2) {
                // 所有格式都失败，抛出异常
                throw new IllegalArgumentException(
                        String.format("无法解析日期字符串 '%s'，支持的格式：yyyy-MM-dd、yyyy/MM/dd", trimmed),
                        e2);
            }
        }
    }
}
