package com.star.pivot.config.mvc;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public final class Converters {
    private Converters() {}

    public static class StringToLongArrayConverter implements Converter<String, Long[]> {
        @Override
        public Long[] convert(@NonNull String source) {
            if (source.isBlank()) {
                return new Long[0];
            }
            return Arrays.stream(source.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toArray(Long[]::new);
        }
    }

    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        );
        @Override
        public LocalDateTime convert(@NonNull String source) {
            if (source.isBlank()) return null;
            String s = source.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(s, formatter);
                } catch (DateTimeParseException ignored) {
                }
            }
            throw new IllegalArgumentException("无法解析日期时间: " + s);
        }
    }

    public static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        );
        @Override
        public LocalDate convert(@NonNull String source) {
            if (source.isBlank()) return null;
            String s = source.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDate.parse(s, formatter);
                } catch (DateTimeParseException ignored) {
                }
            }
            throw new IllegalArgumentException("无法解析日期: " + s);
        }
    }
}
