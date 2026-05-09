package com.star.pivot.config.security;

import com.star.pivot.security.extension.PermitAllPathProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 仅在 local/dev/test 环境放行的运维/调试入口（Swagger / Druid 等）。
 * 生产环境默认不放行，避免暴露内部面。
 */
@Profile({"local", "dev", "test"})
@Component
public class DevToolsPermitAllPathProvider implements PermitAllPathProvider {

    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/druid/**",
                "/api/druid/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }
}

