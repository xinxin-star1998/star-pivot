package com.star.pivot.generator.external;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.generator.domain.external.ExternalDbConnection;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExternalMetadataDialectRegistry {

    private final Map<String, ExternalMetadataDialect> dialectMap;

    public ExternalMetadataDialectRegistry(MySqlMetadataDialect mysql,
            PostgreSqlMetadataDialect postgresql,
            OracleMetadataDialect oracle,
            SqlServerMetadataDialect sqlServer) {
        dialectMap = Map.of(
                "mysql", mysql,
                "postgresql", postgresql,
                "postgres", postgresql,
                "oracle", oracle,
                "sqlserver", sqlServer,
                "mssql", sqlServer);
    }

    public ExternalMetadataDialect get(ExternalDbConnection conn) {
        String dbType = ExternalDataSourceFactory.normalizeDbType(conn.getDbType());
        ExternalMetadataDialect dialect = dialectMap.get(dbType);
        if (dialect == null) {
            throw new BizException("暂不支持的数据库类型：" + dbType);
        }
        return dialect;
    }
}
