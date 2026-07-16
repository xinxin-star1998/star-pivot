package com.star.pivot.security.config;

import com.star.pivot.security.extension.HttpSecurityCustomizer;
import com.star.pivot.security.extension.PermitAllPathProvider;
import com.star.pivot.security.filter.DemoModeFilter;
import com.star.pivot.security.filter.JwtAuthenticationFilter;
import com.star.pivot.security.filter.JwtSecurityExceptionHandler;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({CorsProperties.class, StarPivotSecurityProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DemoModeFilter demoModeFilter;
    private final UserDetailsService userDetailsService;
    private final JwtSecurityExceptionHandler jwtSecurityExceptionHandler;
    private final CorsProperties corsProperties;
    private final StarPivotSecurityProperties securityProperties;
    private final List<PermitAllPathProvider> permitAllPathProviders;
    private final List<HttpSecurityCustomizer> httpSecurityCustomizers;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtSecurityExceptionHandler)
                        .accessDeniedHandler(jwtSecurityExceptionHandler)
                )
                .authorizeHttpRequests(auth -> {
                    String[] permitAllPaths = resolvePermitAllPaths();
                    if (permitAllPaths.length > 0) {
                        auth.requestMatchers(permitAllPaths).permitAll();
                    }

                    if (securityProperties.isSwaggerPermitAll()) {
                        auth.requestMatchers("/doc.html", "/doc.html/**",
                                        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                        "/swagger-resources/**", "/webjars/**")
                                .permitAll();
                    }

                    // SSE 流式响应会触发 ASYNC 二次分发，需在 anyRequest 之前放行
                    auth.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll();

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(demoModeFilter, JwtAuthenticationFilter.class);

        for (HttpSecurityCustomizer customizer : httpSecurityCustomizers) {
            customizer.customize(http);
        }

        return http.build();
    }

    private String[] resolvePermitAllPaths() {
        // 路径均相对于 context-path（/api/v1），无需再写 /api 前缀
        List<String> paths = new ArrayList<>(List.of(
                "/auth/login",
                "/auth/refresh",
                "/auth/captcha",
                "/auth/captcha/verify",
                "/auth/register/enabled",
                "/auth/login/config"
        ));

        if (securityProperties.getPermitAllPaths() != null) {
            paths.addAll(securityProperties.getPermitAllPaths());
        }

        if (permitAllPathProviders != null) {
            for (PermitAllPathProvider provider : permitAllPathProviders) {
                List<String> providerPaths = provider.permitAllPaths();
                if (providerPaths != null) {
                    paths.addAll(providerPaths);
                }
            }
        }

        return paths.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toArray(String[]::new);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String allowedOrigins = corsProperties.getAllowedOrigins();
        
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());

        if (origins.isEmpty()) {
            log.warn("CORS 配置警告：未配置允许的域名，将拒绝所有跨域请求");
            configuration.setAllowedOrigins(Collections.emptyList());
        } else if (origins.contains("*")) {
            log.warn("⚠️ 生产环境不建议使用 '*' 作为 allowedOrigins，这会导致严重的安全风险！");
            log.warn("建议在 application.yml 中明确指定允许的域名列表");
            configuration.addAllowedOriginPattern("*");
            configuration.setAllowCredentials(false);
        } else {
            configuration.setAllowedOrigins(origins);
            configuration.setAllowCredentials(true);
            log.info("CORS 配置已加载，允许的域名: {}", origins);
        }

        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
