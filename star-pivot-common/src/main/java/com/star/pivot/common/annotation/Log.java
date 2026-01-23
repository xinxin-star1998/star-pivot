package com.star.pivot.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    int businessType() default 0;

    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;

    /**
     * 是否保存响应参数
     */
    boolean saveResponseData() default true;
}
