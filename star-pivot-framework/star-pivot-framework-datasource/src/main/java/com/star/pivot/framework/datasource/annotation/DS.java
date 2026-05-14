package com.star.pivot.framework.datasource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定当前类或方法使用的数据源键，需与 YAML 中 {@code star-pivot.multi-datasource.datasources} 的键一致。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

    /** 数据源名称，对应配置中的 key */
    String value();
}
