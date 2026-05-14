package com.star.pivot.framework.datasource.config;

import com.star.pivot.framework.datasource.DynamicRoutingDataSource;
import com.star.pivot.framework.datasource.aspect.DataSourceRouteAspect;
import com.star.pivot.framework.datasource.properties.DataSourceItemProperties;
import com.star.pivot.framework.datasource.properties.MultiDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

/**
 * 启用 {@code star-pivot.multi-datasource.enabled=true} 时，注册路由 {@link DataSource}（{@link Primary}），
 * 并阻止 Spring Boot 再创建默认单数据源。
 */
@AutoConfiguration
@AutoConfigureBefore(
        name = {
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure"
        })
@EnableConfigurationProperties(MultiDataSourceProperties.class)
@ConditionalOnProperty(prefix = "star-pivot.multi-datasource", name = "enabled", havingValue = "true")
public class MultiDataSourceAutoConfiguration {

    @Bean
    @Primary
    public DataSource dataSource(MultiDataSourceProperties properties) {
        Map<Object, Object> target = new HashMap<>();
        for (Map.Entry<String, DataSourceItemProperties> e : properties.getDatasource().entrySet()) {
            target.put(e.getKey(), buildHikari(e.getKey(), e.getValue()));
        }
        if (target.isEmpty()) {
            throw new IllegalStateException("star-pivot.multi-datasource.datasources 不能为空");
        }
        String primary = properties.getPrimary();
        if (!target.containsKey(primary)) {
            throw new IllegalStateException(
                    "star-pivot.multi-datasource.primary=" + primary + " 在 datasources 中不存在");
        }
        DynamicRoutingDataSource routing = new DynamicRoutingDataSource();
        routing.setTargetDataSources(target);
        routing.setDefaultTargetDataSource(target.get(primary));
        routing.afterPropertiesSet();
        return routing;
    }

    @Bean
    public DataSourceRouteAspect dataSourceRouteAspect() {
        return new DataSourceRouteAspect();
    }

    private static HikariDataSource buildHikari(String poolName, DataSourceItemProperties p) {
        if (!StringUtils.hasText(p.getUrl())) {
            throw new IllegalStateException("数据源 [" + poolName + "] 缺少 url");
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("Hikari-" + poolName);
        ds.setJdbcUrl(p.getUrl());
        ds.setUsername(p.getUsername());
        ds.setPassword(p.getPassword());
        if (StringUtils.hasText(p.getDriverClassName())) {
            ds.setDriverClassName(p.getDriverClassName());
        }
        if (p.getMaximumPoolSize() != null) {
            ds.setMaximumPoolSize(p.getMaximumPoolSize());
        }
        if (p.getMinimumIdle() != null) {
            ds.setMinimumIdle(p.getMinimumIdle());
        }
        if (p.getConnectionTimeout() != null) {
            ds.setConnectionTimeout(p.getConnectionTimeout());
        }
        return ds;
    }
}
