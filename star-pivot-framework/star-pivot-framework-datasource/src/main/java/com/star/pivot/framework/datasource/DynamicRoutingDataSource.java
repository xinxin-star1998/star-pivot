package com.star.pivot.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 根据 {@link DataSourceContextHolder} 选择目标 {@link javax.sql.DataSource}。
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.peek();
    }
}
