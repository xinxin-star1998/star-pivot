package com.star.pivot.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数验证注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateParams {

    /**
     * 需要验证的参数名
     */
    String[] value() default {};

    /**
     * 验证规则
     */
    String[] rules() default {};

    /**
     * 错误消息模板
     */
    String[] messages() default {};
}