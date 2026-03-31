# star-pivot-security 使用说明

本文档用于说明如何在其他模块中使用 `star-pivot-security` 提供的扩展点与配置项，实现：
- 通过配置文件追加匿名放行路径
- 通过代码追加匿名放行路径
- 通过代码追加过滤器 / 授权规则（不修改 `SecurityConfig`）

## 方式1：配置文件追加放行路径

在业务模块的 `application.yml`（或对应环境配置文件）中增加：

```yaml
security:
  permit-all-paths:
    - /public/**
    - /actuator/health

cors:
  allowed-origins: "*"
```

说明：
- `security.permit-all-paths`：额外匿名放行路径（Ant 风格）。
- `cors.allowed-origins`：允许跨域来源。配置为 `*` 表示允许所有域名。

## 方式2：其他模块追加放行路径（代码扩展）

在其他模块写一个 `@Component` 实现 `PermitAllPathProvider`，返回路径列表即可。

```java
package com.star.pivot.xxx;

import com.star.pivot.security.extension.PermitAllPathProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 业务模块追加匿名放行路径示例
 */
@Component
public class DemoPermitAllPathProvider implements PermitAllPathProvider {
    @Override
    public List<String> permitAllPaths() {
        return List.of(
                "/public/**",
                "/actuator/health"
        );
    }
}
```

## 方式3：其他模块追加过滤器/授权规则（代码扩展）

在其他模块写一个 `@Component` 实现 `HttpSecurityCustomizer`，在 `customize(HttpSecurity http)` 里调用
`addFilterBefore` / `addFilterAfter` / `authorizeHttpRequests` 等即可。

```java
package com.star.pivot.xxx;

import com.star.pivot.security.extension.HttpSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * 业务模块自定义 HttpSecurity 示例
 */
@Component
public class DemoHttpSecurityCustomizer implements HttpSecurityCustomizer {
    @Override
    public void customize(HttpSecurity http) throws Exception {
        // 示例：追加授权规则（按需调整）
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/demo/**").permitAll()
        );

        // 示例：追加过滤器（按需调整）
        // http.addFilterBefore(new YourFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

