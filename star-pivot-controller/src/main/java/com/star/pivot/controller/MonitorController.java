package com.star.pivot.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.DruidMonitorVO;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.domain.bo.RedisCacheVO;
import com.star.pivot.system.domain.bo.ServerInfoVO;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.ApiPerformanceReqBo;
import com.star.pivot.system.service.MonitorService;
import com.star.pivot.system.service.impl.MonitorServiceImpl;
import com.star.pivot.system.domain.entity.SysMonitorApiPerformance;
import com.star.pivot.system.mapper.SysMonitorApiPerformanceMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 系统监控控制器
 *
 * @author xinxin
 * @since 2026-01-25
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    private final MonitorService monitorService;
    private final com.star.pivot.config.ApiPerformanceAspect apiPerformanceAspect;

    public MonitorController(MonitorService monitorService,
                            com.star.pivot.config.ApiPerformanceAspect apiPerformanceAspect) {
        this.monitorService = monitorService;
        this.apiPerformanceAspect = apiPerformanceAspect;
    }

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    @Log(title = "服务器监控")
    @PreAuthorize("hasAuthority('monitor:server:query')")
    @GetMapping("/server")
    public Result<ServerInfoVO> getServerInfo() {
        ServerInfoVO serverInfo = monitorService.getServerInfo();
        return Result.success(serverInfo);
    }

    /**
     * 获取 Druid 监控信息
     * <p>
     * 支持通过参数控制是否返回慢SQL列表，实现慢SQL分析与Druid监控的合并
     * </p>
     *
     * @param includeSlowSqlList 是否包含慢SQL列表（可选，默认false）
     * @param slowSqlThreshold 慢SQL阈值（毫秒，可选，默认5000，仅在 includeSlowSqlList=true 时有效）
     * @return Druid 监控信息（包含慢SQL列表，如果 includeSlowSqlList=true）
     */
    @Log(title = "Druid监控")
    @PreAuthorize("hasAuthority('monitor:druid:query')")
    @GetMapping("/druid")
    public Result<DruidMonitorVO> getDruidMonitorInfo(
            @RequestParam(required = false, defaultValue = "false") Boolean includeSlowSqlList,
            @RequestParam(required = false) Long slowSqlThreshold) {
        // 调用服务方法获取监控信息
        DruidMonitorVO druidInfo;
        if (includeSlowSqlList != null && includeSlowSqlList) {
            druidInfo = monitorService.getDruidMonitorInfoWithSlowSql(slowSqlThreshold);
        } else {
            druidInfo = monitorService.getDruidMonitorInfo();
        }
        return Result.success(druidInfo);
    }

    /**
     * 获取在线用户列表
     * <p>
     * 说明：当前为简化实现，基于 JWT 无状态认证。
     * 通过 Redis 中存储的刷新令牌（jwt:refresh:user:{userId}）来判断用户是否在线。
     * 后续可按需优化为更完善的会话管理方案（如：引入 Session 机制、记录登录 IP/设备信息等）。
     * </p>
     *
     * @param userName 用户名（可选）
     * @param ipaddr   IP地址（可选，当前实现暂不支持）
     * @return 在线用户列表
     */
    @Log(title = "在线用户")
    @PreAuthorize("hasAuthority('monitor:online:query')")
    @GetMapping("/online")
    public Result<List<OnlineUserVO>> getOnlineUserList(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String ipaddr) {
        List<OnlineUserVO> onlineUserList = monitorService.getOnlineUserList(userName, ipaddr);
        return Result.success(onlineUserList);
    }

    /**
     * 强制用户下线
     * <p>
     * 说明：通过删除 Redis 中的刷新令牌来实现强制下线。
     * 删除刷新令牌后，用户无法刷新 JWT Token，从而间接实现下线效果。
     * </p>
     *
     * @param sessionId 会话ID（实际为 Redis 中的刷新令牌 key，格式：jwt:refresh:user:{userId}）
     * @return 操作结果
     */
    @Log(title = "在线用户", businessType = 3)
    @PreAuthorize("hasAuthority('monitor:online:force-logout')")
    @DeleteMapping("/online/{sessionId}")
    public Result<?> forceLogout(@PathVariable("sessionId") String sessionId) {
        boolean success = monitorService.forceLogout(sessionId);
        return success ? Result.success("强制下线成功") : Result.error("强制下线失败");
    }


    /**
     * 获取系统健康检查报告
     *
     * @return 健康检查报告
     */
    @Log(title = "系统健康检查")
    @PreAuthorize("hasAuthority('monitor:health:query')")
    @GetMapping("/health")
    public Result<Map<String, Object>> getHealthCheck() {
        Map<String, Object> healthReport = monitorService.getHealthCheck();
        return Result.success(healthReport);
    }


    /**
     * 获取缓存列表
     *
     * @return 缓存列表
     */
    @Log(title = "Redis缓存管理")
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/cache/list")
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
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/cache/keys")
    public Result<List<RedisCacheVO.CacheKeyInfo>> getCacheKeys(@RequestParam String cacheName) {
        List<RedisCacheVO.CacheKeyInfo> keys = monitorService.getCacheKeys(cacheName);
        return Result.success(keys);
    }

    /**
     * 获取缓存内容
     *
     * @param cacheName 缓存名称
     * @param key 缓存键名
     * @return 缓存内容
     */
    @Log(title = "Redis缓存管理")
    @PreAuthorize("hasAuthority('monitor:cache:query')")
    @GetMapping("/cache/content")
    public Result<RedisCacheVO.CacheContentInfo> getCacheContent(
            @RequestParam String cacheName,
            @RequestParam String key) {
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
    @PreAuthorize("hasAuthority('monitor:cache:remove')")
    @DeleteMapping("/cache/{cacheName}")
    public Result<Long> deleteCache(@PathVariable String cacheName) {
        long deletedCount = monitorService.deleteCache(cacheName);
        return Result.success("删除成功，共删除 " + deletedCount + " 个键", deletedCount);
    }

    /**
     * 删除单个缓存键
     *
     * @param cacheName 缓存名称
     * @param key 缓存键名
     * @return 操作结果
     */
    @Log(title = "Redis缓存管理", businessType = 3)
    @PreAuthorize("hasAuthority('monitor:cache:remove')")
    @DeleteMapping("/cache/key")
    public Result<?> deleteCacheKey(
            @RequestParam String cacheName,
            @RequestParam String key) {
        boolean success = monitorService.deleteCacheKey(cacheName, key);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空所有缓存
     *
     * @return 操作结果
     */
    @Log(title = "Redis缓存管理", businessType = 9)
    @PreAuthorize("hasAuthority('monitor:cache:clear')")
    @DeleteMapping("/cache/clear")
    public Result<?> clearAllCache() {
        boolean success = monitorService.clearAllCache();
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 获取最慢的API接口（Top N）
     *
     * @param limit 查询数量限制
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return API性能数据列表
     */
    @Log(title = "API性能监控")
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/api/slowest")
    public Result<List<SysMonitorApiPerformance>> getSlowestApis(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        // 参数验证
        if (startDate == null || endDate == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始日期不能晚于结束日期");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            return Result.error("limit 参数必须在 1-100 之间");
        }
        
        List<SysMonitorApiPerformance> apis = monitorService.getSlowestApis(limit, startDate, endDate);
        return Result.success(apis);
    }

    /**
     * 获取错误率最高的API接口（Top N）
     *
     * @param limit 查询数量限制
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return API性能数据列表
     */
    @Log(title = "API性能监控")
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/api/error-rate")
    public Result<List<SysMonitorApiPerformance>> getHighestErrorRateApis(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        // 参数验证
        if (startDate == null || endDate == null) {
            return Result.error("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始日期不能晚于结束日期");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            return Result.error("limit 参数必须在 1-100 之间");
        }
        
        List<SysMonitorApiPerformance> apis = monitorService.getHighestErrorRateApis(limit, startDate, endDate);
        return Result.success(apis);
    }

    /**
     * 分页查询API性能监控数据
     *
     * @param reqBo 查询参数
     * @return 分页结果
     */
    @Log(title = "API性能监控")
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @PostMapping("/api/pageList")
    public Result<PageResponse<SysMonitorApiPerformance>> getApiPerformancePageList(@RequestBody ApiPerformanceReqBo reqBo) {
        PageResponse<SysMonitorApiPerformance> pageResponse = monitorService.getApiPerformancePageList(reqBo);
        return Result.success(pageResponse);
    }

    /**
     * 获取API性能监控统计信息
     * 
     * @return 统计信息（总请求数、慢接口数、采样数、队列大小、配置参数等）
     */
    @Log(title = "API性能监控")
    @PreAuthorize("hasAuthority('monitor:api:query')")
    @GetMapping("/api/stats")
    public Result<com.star.pivot.config.ApiPerformanceAspect.ApiPerformanceStats> getApiPerformanceStats() {
        if (apiPerformanceAspect == null) {
            return Result.error("API性能监控未启用");
        }
        com.star.pivot.config.ApiPerformanceAspect.ApiPerformanceStats stats = apiPerformanceAspect.getStats();
        return Result.success(stats);
    }
}
