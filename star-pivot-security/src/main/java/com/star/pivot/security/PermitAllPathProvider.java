package com.star.pivot.security;

import java.util.List;

/**
 * 放行路径扩展点
 * <p>
 * 其他模块如需追加匿名访问路径，可实现该接口并注册为 Spring Bean。
 */
public interface PermitAllPathProvider {

    /**
     * 返回需要匿名访问的路径列表（Ant 风格），例如：/public/**、/actuator/health
     */
    List<String> permitAllPaths();
}

