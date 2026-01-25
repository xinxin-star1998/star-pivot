package com.star.pivot.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.util.Arrays;

/**
 * 将逗号分隔的字符串转换为 Long 数组
 * 用于 @PathVariable 解析如 "/1,2,3" 形式的批量删除接口
 *
 * @author StarPivot
 */
public class StringToLongArrayConverter implements Converter<String, Long[]> {

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
