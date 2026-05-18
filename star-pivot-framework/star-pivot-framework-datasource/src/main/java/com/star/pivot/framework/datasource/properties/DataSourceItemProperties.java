package com.star.pivot.framework.datasource.properties;

import lombok.Data;

@Data
public class DataSourceItemProperties {

    private String url;
    private String username;
    private String password;
    /** 可选；未设置时由 JDBC 驱动推断 */
    private String driverClassName;
    /** Hikari 最大连接数 */
    private Integer maximumPoolSize = 10;
    /** Hikari 最小空闲连接 */
    private Integer minimumIdle;
    /** 连接超时毫秒 */
    private Long connectionTimeout;
}
