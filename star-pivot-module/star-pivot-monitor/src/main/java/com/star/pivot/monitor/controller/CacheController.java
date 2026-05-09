package com.star.pivot.monitor.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.monitor.domain.vo.RedisCacheVO;
import com.star.pivot.monitor.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Redis 缓存管理控制器
 *
 * @author xinxin
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/monitor/cache")
@Tag(name = "Redis缓存管理", description = "Redis缓存查询、删除、清空等管理接口")
@RequiredArgsConstructor
public class CacheController {

    private final MonitorService monitorService;

    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    @Log(title = "Redis缓存管理")
    @Operation(summary = "获取缓存列表", description = "获取所有缓存名称及其统计信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = RedisCacheVO.class)))
    })
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/list")
    public Result<List<RedisCacheVO>> getCacheList() {
        List<RedisCacheVO> cacheList = monitorService.getCacheList();
        return Result.success(cacheList);
    }

    /**
     * 根据缓存名称获取键名列表
     *
     * @param cacheName 缓存名称
     * @return 键名列表
     */
    @Log(title = "Redis缓存管理")
    @Operation(summary = "获取缓存键名列表", description = "根据缓存名称获取该缓存下的所有键名")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/keys")
    public Result<List<RedisCacheVO.CacheKeyInfo>> getCacheKeys(
            @Parameter(description = "缓存名称") @RequestParam String cacheName) {
        List<RedisCacheVO.CacheKeyInfo> keys = monitorService.getCacheKeys(cacheName);
        return Result.success(keys);
    }

    /**
     * 获取缓存内容
     *
     * @param cacheName 缓存名称
     * @param key       缓存键名
     * @return 缓存内容
     */
    @Log(title = "Redis缓存管理")
    @Operation(summary = "获取缓存内容", description = "根据缓存名称和键名查看缓存内容")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = RedisCacheVO.CacheContentInfo.class)))
    })
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/content")
    public Result<RedisCacheVO.CacheContentInfo> getCacheContent(
            @Parameter(description = "缓存名称") @RequestParam String cacheName,
            @Parameter(description = "缓存键名") @RequestParam String key) {
        RedisCacheVO.CacheContentInfo content = monitorService.getCacheContent(cacheName, key);
        return Result.success(content);
    }

    /**
     * 删除缓存（根据缓存名称删除所有匹配的键）
     *
     * @param cacheName 缓存名称
     * @return 删除的键数量
     */
    @Log(title = "Redis缓存管理", businessType = 3)
    @Operation(summary = "删除缓存", description = "根据缓存名称删除该缓存下的所有键")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    @PreAuthorize("hasAuthority('monitor:cache:remove')")
    @DeleteMapping("/group/{cacheName}")
    public Result<Long> deleteCache(@Parameter(description = "缓存名称（Redis 键前缀分组）") @PathVariable String cacheName) {
        long deletedCount = monitorService.deleteCache(cacheName);
        return Result.success("删除成功，共删除 " + deletedCount + " 个键", deletedCount);
    }

    /**
     * 删除单个缓存键
     *
     * @param cacheName 缓存名称
     * @param key       缓存键名
     * @return 操作结果
     */
    @Log(title = "Redis缓存管理", businessType = 3)
    @Operation(summary = "删除单个缓存键", description = "删除指定缓存下的单个键")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    @PreAuthorize("hasAuthority('monitor:cache:remove')")
    @DeleteMapping("/key")
    public Result<?> deleteCacheKey(
            @Parameter(description = "缓存名称") @RequestParam String cacheName,
            @Parameter(description = "缓存键名") @RequestParam String key) {
        boolean success = monitorService.deleteCacheKey(cacheName, key);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空所有缓存
     *
     * @return 操作结果
     */
    @Log(title = "Redis缓存管理", businessType = 9)
    @Operation(summary = "清空所有缓存", description = "清空所有Redis缓存，请谨慎操作")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "清空成功"),
            @ApiResponse(responseCode = "400", description = "清空失败")
    })
    @PreAuthorize("hasAuthority('monitor:cache:clear')")
    @DeleteMapping("/clear")
    public Result<?> clearAllCache() {
        boolean success = monitorService.clearAllCache();
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }
}
