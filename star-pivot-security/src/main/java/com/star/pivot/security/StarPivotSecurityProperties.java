package com.star.pivot.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全相关配置
 */
@ConfigurationProperties(prefix = "security")
public class StarPivotSecurityProperties {

    /**
     * 是否允许直接访问 Swagger/Knife4j 文档
     * 非生产环境默认开启，生产环境建议通过配置关闭：security.swagger-permit-all=false
     */
    private boolean swaggerPermitAll = true;

    /**
     * 额外放行的路径（匿名访问），支持 Ant 风格路径，如：/public/**、/health
     */
    private List<String> permitAllPaths = new ArrayList<>();

    public boolean isSwaggerPermitAll() {
        return swaggerPermitAll;
    }

    public void setSwaggerPermitAll(boolean swaggerPermitAll) {
        this.swaggerPermitAll = swaggerPermitAll;
    }

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }
}

