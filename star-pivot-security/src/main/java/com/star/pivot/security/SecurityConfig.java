package com.star.pivot.security;

import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({CorsProperties.class, StarPivotSecurityProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final JwtSecurityExceptionHandler jwtSecurityExceptionHandler;
    private final CorsProperties corsProperties;
    private final StarPivotSecurityProperties securityProperties;
    private final List<PermitAllPathProvider> permitAllPathProviders;
    private final List<HttpSecurityCustomizer> httpSecurityCustomizers;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * 认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security 过滤器链配置
     *
     * <p>外部调用统一以 {@code /api} 为前缀（由 {@code server.servlet.context-path=/api} 决定），
     * 这里的 {@code /auth/login} 等路径都是去掉 context-path 之后的应用内路径。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（因为使用 JWT）
                .csrf(AbstractHttpConfigurer::disable)
                // 启用 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置认证提供者
                .authenticationProvider(authenticationProvider())
                // 使用无状态会话（JWT）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtSecurityExceptionHandler)
                        .accessDeniedHandler(jwtSecurityExceptionHandler)
                )
                // 配置请求授权
                .authorizeHttpRequests(auth -> {
                    // 允许登录和验证码相关接口匿名访问（注意：如果配置了context-path，Spring Security会自动处理）
                    String[] permitAllPaths = resolvePermitAllPaths();
                    if (permitAllPaths.length > 0) {
                        auth.requestMatchers(permitAllPaths).permitAll();
                    }

                    // 是否允许 Swagger 相关路径匿名访问
                    if (securityProperties.isSwaggerPermitAll()) {
                        auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                        "/swagger-resources/**", "/webjars/**")
                                .permitAll();
                    }

                    // 其他请求需要认证（包括菜单接口，根据用户权限返回菜单）
                    auth.anyRequest().authenticated();
                })
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 额外扩展：允许其他模块对 HttpSecurity 进行二次定制（例如追加过滤器、追加授权规则等）
        for (HttpSecurityCustomizer customizer : httpSecurityCustomizers) {
            customizer.customize(http);
        }

        return http.build();
    }

    /**
     * 汇总放行路径：
     * <ul>
     *   <li>安全模块内置默认放行（登录/验证码）</li>
     *   <li>配置项 security.permit-all-paths</li>
     *   <li>其他模块通过 PermitAllPathProvider 提供的扩展放行</li>
     * </ul>
     */
    private String[] resolvePermitAllPaths() {

        // 内置默认放行路径
        List<String> paths = new ArrayList<>(List.of(
                "/auth/login",
                "/auth/refresh",
                "/auth/captcha",
                "/auth/captcha/verify"
        ));

        // 配置文件追加放行路径
        if (securityProperties.getPermitAllPaths() != null) {
            paths.addAll(securityProperties.getPermitAllPaths());
        }

        // 其他模块追加放行路径
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
        
        // 根据配置决定使用允许的域名列表还是允许所有域名
        String allowedOrigins = corsProperties.getAllowedOrigins();
        if ("*".equals(allowedOrigins)) {
            // 开发环境：允许所有域名
            configuration.addAllowedOriginPattern("*");
            // 允许所有域名时，不能携带凭证（浏览器规范限制）
            configuration.setAllowCredentials(false);
        } else {
            // 生产环境：配置具体的允许域名
            List<String> origins = Arrays.asList(allowedOrigins.split(","));
            configuration.setAllowedOrigins(origins);
            // 配置了明确允许域名时，才允许携带凭证
            configuration.setAllowCredentials(true);
        }
        
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
