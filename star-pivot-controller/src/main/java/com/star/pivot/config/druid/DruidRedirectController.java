package com.star.pivot.config.druid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Druid 监控页重定向。
 * 项目配置了 context-path=/api，正确地址为：/api/druid/index.html
 * 访问 /api/druid 或 /api/druid/ 时重定向到 index.html，避免 404。
 */
@Controller
public class DruidRedirectController {

    @GetMapping({ "/druid", "/druid/" })
    public String druidRedirect() {
        return "redirect:/druid/index.html";
    }
}
