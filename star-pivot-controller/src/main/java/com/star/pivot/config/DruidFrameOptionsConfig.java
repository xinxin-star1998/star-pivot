package com.star.pivot.config;

import com.star.pivot.config.filter.DruidFrameOptionsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid Frame Options 配置类
 *
 * <p>职责：注册 Druid Frame Options 过滤器
 *
 * <p>功能说明：
 * <ul>
 *   <li>允许 Druid 监控页面在 iframe 中加载</li>
 *   <li>解决 X-Frame-Options 限制问题</li>
 *   <li>保护其他页面不被嵌入到 iframe 中</li>
 * </ul>
 *
 * @author xinxin
 */
@Configuration
public class DruidFrameOptionsConfig {

    /**
     * 注册 Druid Frame Options 过滤器
     *
     * <p>配置说明：
     * <ul>
     *   <li>过滤器顺序：最高优先级（@Order(Ordered.HIGHEST_PRECEDENCE)）</li>
     *   <li>URL 模式：/*（匹配所有请求）</li>
     *   <li>过滤逻辑：Druid 路径移除 X-Frame-Options，其他路径设置为 DENY</li>
     * </ul>
     *
     * @return FilterRegistrationBean 实例
     */
    @Bean
    public FilterRegistrationBean<DruidFrameOptionsFilter> frameOptionsFilter() {
        FilterRegistrationBean<DruidFrameOptionsFilter> registration =
                new FilterRegistrationBean<>();
        registration.setFilter(new DruidFrameOptionsFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
        registration.setName("druidFrameOptionsFilter");
        return registration;
    }
}
