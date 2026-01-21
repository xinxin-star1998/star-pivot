package com.star.pivot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * CORS允许的域名，多个用逗号分隔
     * 生产环境建议通过环境变量配置：CORS_ALLOWED_ORIGINS=https://example.com,https://www.example.com
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    /**
     * 是否允许直接访问 Swagger/Knife4j 文档
     * 非生产环境默认开启，生产环境建议通过配置关闭：security.swagger-permit-all=false
     */
    @Value("${security.swagger-permit-all:true}")
    private boolean swaggerPermitAll;

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
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                // 配置请求授权
                .authorizeHttpRequests(auth -> {
                    // 允许登录和验证码相关接口匿名访问（注意：如果配置了context-path，Spring Security会自动处理）
                    auth.requestMatchers(
                                    "/auth/login",
                                    "/api/auth/login",
                                    "/auth/captcha",
                                    "/api/auth/captcha",
                                    "/auth/captcha/verify",
                                    "/api/auth/captcha/verify"
                            )
                            .permitAll();

                    // 是否允许 Swagger 相关路径匿名访问
                    if (swaggerPermitAll) {
                        auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                        "/swagger-resources/**", "/webjars/**")
                                .permitAll();
                    }

                    // 其他请求需要认证（包括菜单接口，根据用户权限返回菜单）
                    auth.anyRequest().authenticated();
                })
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 根据配置决定使用允许的域名列表还是允许所有域名
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
