package com.star.pivot.common.annotation;

import java.lang.annotation.*;

/**
 * 权限控制注解
 * 用于标记需要权限验证的接口
 *
 * @author stardust
 * @date 2024-01-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorize {

    /**
     * 权限标识
     * 例如：system:user:add, system:user:edit
     */
    String value() default "";

    /**
     * 是否必须拥有该权限
     * true: 必须拥有该权限才能访问
     * false: 如果没有该权限，则跳过权限检查（但超级管理员仍然可以访问）
     */
    boolean required() default true;
}

