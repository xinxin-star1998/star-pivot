package com.star.pivot.config;

import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Druid 注册状态检查
 * 应用启动时自动检查 Druid StatViewServlet 是否已正确注册
 */
@Slf4j
@Component
@Order(1) // 确保在其他检查之前执行
public class DruidHealthCheck implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    public DruidHealthCheck(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("开始检查 Druid StatViewServlet 注册状态...");
        log.info("==========================================");

        // 方法1：检查 ServletRegistrationBean
        checkServletRegistrationBeans();

        // 方法2：检查所有注册的 Servlet
        checkRegisteredServlets();

        log.info("==========================================");
        log.info("Druid 注册状态检查完成");
        log.info("==========================================");
    }

    /**
     * 检查 ServletRegistrationBean 类型的 Bean
     */
    @SuppressWarnings("unchecked")
    private void checkServletRegistrationBeans() {
        try {
            Map<String, ServletRegistrationBean<?>> servletBeans =
                    (Map<String, ServletRegistrationBean<?>>) (Map<?, ?>) applicationContext.getBeansOfType(ServletRegistrationBean.class);

            log.info("找到 {} 个 ServletRegistrationBean", servletBeans.size());

            boolean druidFound = false;
            for (Map.Entry<String, ServletRegistrationBean<?>> entry : servletBeans.entrySet()) {
                String beanName = entry.getKey();
                ServletRegistrationBean<?> bean = entry.getValue();
                Servlet servlet = bean.getServlet();

                log.info("  - Bean名称: {}", beanName);
                log.info("    Servlet类型: {}", servlet != null ? servlet.getClass().getName() : "null");
                log.info("    URL映射: {}", bean.getUrlMappings());

                // 检查是否是 Druid 的 StatViewServlet
                if (servlet != null && servlet.getClass().getName().contains("StatViewServlet")) {
                    druidFound = true;
                    log.info("    ✓ 找到 Druid StatViewServlet！");
                    log.info("    访问地址: http://localhost:8080/api/druid/index.html");
                }
            }

            if (!druidFound) {
                log.warn("  ⚠ 未找到 Druid StatViewServlet 的 ServletRegistrationBean");
                log.warn("  请检查 druid-spring-boot-starter 自动配置是否生效");
            }
        } catch (Exception e) {
            log.error("检查 ServletRegistrationBean 时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查所有已注册的 Servlet（通过 ServletContext）
     * 注意：Druid 的 Servlet 是通过 ServletRegistrationBean 注册的，不是直接作为 Bean，
     * 所以这里可能找不到，这是正常现象
     */
    private void checkRegisteredServlets() {
        try {
            // 尝试从 ApplicationContext 获取 ServletContext
            // 注意：在 CommandLineRunner 中，ServletContext 可能还未完全初始化
            log.debug("检查已注册的 Servlet...");

            // 查找所有包含 "druid" 或 "StatView" 的 Bean
            String[] beanNames = applicationContext.getBeanNamesForType(Servlet.class);
            boolean druidFound = false;

            for (String beanName : beanNames) {
                Servlet servlet = applicationContext.getBean(beanName, Servlet.class);
                String className = servlet.getClass().getName();

                if (className.contains("StatViewServlet") || className.contains("druid")) {
                    druidFound = true;
                    log.info("  ✓ 找到 Druid 相关 Servlet: {} ({})", beanName, className);
                }
            }

            // 这是正常的，因为 Druid Servlet 通过 ServletRegistrationBean 注册，不是直接作为 Bean
            if (!druidFound) {
                log.debug("  ℹ Druid Servlet 通过 ServletRegistrationBean 注册（已在上面检查中确认）");
            }
        } catch (Exception e) {
            log.debug("检查已注册 Servlet 时出错（可能 ServletContext 尚未初始化）: {}", e.getMessage());
        }
    }
}
