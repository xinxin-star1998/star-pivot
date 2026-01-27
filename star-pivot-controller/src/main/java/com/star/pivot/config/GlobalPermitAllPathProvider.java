package com.star.pivot.config;

import com.star.pivot.security.PermitAllPathProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Security 匿名放行路径集中配置
 * <p>
 * 统一维护所有无需登录即可访问的接口路径
 */
@Component
public class GlobalPermitAllPathProvider implements PermitAllPathProvider {

    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/druid/**",  // 放行 Druid 内置监控页面（去掉 context-path 后的应用内路径）
                "/swagger-ui/**",  // 放行 Swagger UI 页面
                "/v3/api-docs/**",  // 放行 OpenAPI JSON 文档
                "/swagger-resources/**",  // 放行 Swagger 资源文件
                "/webjars/**"  // 放行 Swagger UI 所需的静态资源
        );
    }
}
