package com.star.pivot.config.security;

import com.star.pivot.security.extension.PermitAllPathProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalPermitAllPathProvider implements PermitAllPathProvider {

    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/druid/**",
                "/api/druid/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/auth/register"
        );
    }
}
