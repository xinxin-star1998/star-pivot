package com.star.pivot.framework.boot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "star-pivot")
public class StarPivotProperties {

    private Cache cache = new Cache();

    @Data
    public static class Cache {
        private Long menuTreeTtl = 3600L;
        private Long dictDataTtl = 3600L;
        private Long userPermissionsTtl = 1800L;
    }
}

