package com.star.pivot.config;

import com.star.pivot.security.PermitAllPathProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Druid 监控页面路径放行提供者
 * <p>
 * 用于放行 Druid 内置监控页面，使其可以匿名访问
 */
@Component
public class DruidPermitAllPathProvider implements PermitAllPathProvider {

    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/druid/**"  // 放行 Druid 内置监控页面（去掉 context-path 后的应用内路径）
        );
    }
}
