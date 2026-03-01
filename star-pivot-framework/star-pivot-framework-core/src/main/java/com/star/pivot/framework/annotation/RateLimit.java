package com.star.pivot.framework.annotation;

import java.lang.annotation.*;

/**
 * API限流注解
 * 
 * <p>使用示例：
 * <pre>
 * &#64;RateLimit(key = "user:list", maxRequests = 100, windowSeconds = 60)
 * public Result&lt;List&lt;User&gt;&gt; listUsers() { ... }
 * </pre>
 * 
 * <p>支持两种限流维度：
 * <ul>
 *   <li>IP维度：基于客户端IP地址限流</li>
 *   <li>用户维度：基于当前登录用户ID限流</li>
 * </ul>
 *
 * @author xinxin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key前缀，用于区分不同的接口
     * 默认为空，会自动使用类名+方法名
     */
    String key() default "";

    /**
     * 时间窗口内最大请求数
     */
    int maxRequests() default 100;

    /**
     * 时间窗口（秒）
     */
    int windowSeconds() default 60;

    /**
     * 限流维度
     */
    LimitType limitType() default LimitType.IP;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流维度枚举
     */
    enum LimitType {
        /**
         * 基于IP限流
         */
        IP,
        /**
         * 基于用户ID限流
         */
        USER,
        /**
         * 全局限流（所有请求共享配额）
         */
        GLOBAL
    }
}
