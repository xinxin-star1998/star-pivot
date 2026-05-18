package com.star.pivot.framework.datasource.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "star-pivot.multi-datasource")
public class MultiDataSourceProperties {

    /** 是否启用多数据源自动配置 */
    private boolean enabled = false;
    /** 未指定 @DS 或上下文为空时使用的数据源键 */
    private String primary = "master";
    /** 数据源名称 → JDBC 配置 */
    private Map<String, DataSourceItemProperties> datasource = new LinkedHashMap<>();
}
