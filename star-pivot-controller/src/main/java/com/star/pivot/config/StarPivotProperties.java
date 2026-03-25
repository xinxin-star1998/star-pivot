package com.star.pivot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * StarPivot 中与 {@code star-pivot.cache.*} 对应的配置。
 *
 * <p>JWT 使用顶层 {@code jwt.*}；监控使用 {@code star-pivot.monitor.*}（由监控模块单独绑定）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "star-pivot")
public class StarPivotProperties {

    private Cache cache = new Cache();

    @Data
    public static class Cache {
        /** 菜单树等「树/列表」类缓存的基础过期时间（秒），默认 1 小时 */
        private Long menuTreeTtl = 3600L;
        /** 字典等数据缓存的基础过期时间（秒），默认 1 小时 */
        private Long dictDataTtl = 3600L;
        /** 用户权限缓存的基础过期时间（秒），默认 30 分钟 */
        private Long userPermissionsTtl = 1800L;
    }
}
