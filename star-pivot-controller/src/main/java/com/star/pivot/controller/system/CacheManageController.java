package com.star.pivot.controller.system;

import com.star.pivot.framework.cache.CacheHelper;
import com.star.pivot.framework.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存管理控制器
 * <p>
 * 提供缓存管理接口，用于查看缓存状态、清除缓存等操作
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@RestController
@RequestMapping("/monitor/cache")
@RequiredArgsConstructor
@Tag(name = "缓存管理", description = "缓存状态查看、清除等管理接口")
public class CacheManageController {

    private final CacheManager cacheManager;
    private final CacheHelper cacheHelper;

    /**
     * 获取所有缓存名称
     */
    @Operation(summary = "获取缓存名称列表", description = "获取系统中所有缓存的名称列表")
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/names")
    public Result<Set<String>> getCacheNames() {
        Set<String> cacheNames = cacheHelper.getCacheNames();
        return Result.success(cacheNames);
    }

    /**
     * 获取缓存统计信息
     */
    @Operation(summary = "获取缓存统计", description = "获取所有缓存的统计信息（缓存名称、键数量等）")
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/stats")
    public Result<List<CacheStats>> getCacheStats() {
        List<CacheStats> stats = new ArrayList<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();

        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // 注意：Spring Cache 不直接提供缓存大小统计
                // 这里仅返回缓存名称，实际大小需要通过 Redis 命令查询
                CacheStats stat = new CacheStats();
                stat.setCacheName(cacheName);
                stat.setDescription(getCacheDescription(cacheName));
                stats.add(stat);
            }
        }

        return Result.success(stats);
    }

    /**
     * 清除指定缓存
     */
    @Operation(summary = "清除缓存", description = "根据缓存名称清除整个缓存")
    @PreAuthorize("hasAuthority('monitor:cache:clear')")
    @DeleteMapping("/clear/{cacheName}")
    public Result<Void> clearCache(@PathVariable String cacheName) {
        log.info("手动清除缓存: {}", cacheName);
        cacheHelper.clear(cacheName);
        return Result.success("缓存清除成功");
    }

    /**
     * 清除指定缓存键
     */
    @Operation(summary = "清除缓存键", description = "清除指定缓存中的特定键")
    @PreAuthorize("hasAuthority('monitor:cache:clear')")
    @DeleteMapping("/evict")
    public Result<Void> evictCacheKey(@RequestParam String cacheName, @RequestParam String key) {
        log.info("手动清除缓存键: {}.{}", cacheName, key);
        cacheHelper.evict(cacheName, key);
        return Result.success("缓存键清除成功");
    }

    /**
     * 批量清除缓存
     */
    @Operation(summary = "批量清除缓存", description = "批量清除多个缓存")
    @PreAuthorize("hasAuthority('monitor:cache:clear')")
    @DeleteMapping("/clear/batch")
    public Result<Void> clearBatchCache(@RequestBody List<String> cacheNames) {
        log.info("批量清除缓存: {}", cacheNames);
        for (String cacheName : cacheNames) {
            cacheHelper.clear(cacheName);
        }
        return Result.success("批量缓存清除成功");
    }

    /**
     * 获取缓存描述
     */
    private String getCacheDescription(String cacheName) {
        return switch (cacheName) {
            case "userPermissions" -> "用户权限缓存";
            case "userRoles" -> "用户角色缓存";
            case "menuTree" -> "菜单树缓存";
            case "dictData" -> "字典数据缓存";
            case "dictType" -> "字典类型缓存";
            case "deptTree" -> "部门树缓存";
            case "postList" -> "岗位列表缓存";
            case "roleList" -> "角色列表缓存";
            case "sysConfig" -> "系统配置缓存";
            case "onlineUser" -> "在线用户缓存";
            case "captcha" -> "验证码缓存";
            default -> "其他缓存";
        };
    }

    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private String cacheName;
        private String description;

        public String getCacheName() {
            return cacheName;
        }

        public void setCacheName(String cacheName) {
            this.cacheName = cacheName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
