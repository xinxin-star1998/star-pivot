package com.star.pivot.system.service;

import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.ApiPerformanceReqBo;
import com.star.pivot.system.domain.bo.DruidMonitorVO;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.domain.bo.RedisCacheVO;
import com.star.pivot.system.domain.bo.ServerInfoVO;
import com.star.pivot.system.domain.entity.SysMonitorApiPerformance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 监控服务接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
public interface MonitorService {

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    ServerInfoVO getServerInfo();

    /**
     * 获取 Druid 监控信息
     *
     * @return Druid 监控信息
     */
    DruidMonitorVO getDruidMonitorInfo();

    /**
     * 获取包含慢SQL列表的 Druid 监控信息
     *
     * @param slowSqlThreshold 慢SQL阈值（毫秒），null 则使用默认值 5000
     * @return Druid 监控信息（包含慢SQL列表）
     */
    DruidMonitorVO getDruidMonitorInfoWithSlowSql(Long slowSqlThreshold);

    /**
     * 获取在线用户列表
     * <p>
     * 说明：当前为简化实现，基于 JWT 无状态认证。
     * 通过 Redis 中存储的刷新令牌（jwt:refresh:user:{userId}）来判断用户是否在线。
     * 后续可按需优化为更完善的会话管理方案。
     * </p>
     *
     * @param userName 用户名（可选，用于过滤）
     * @param ipaddr   IP地址（可选，当前实现暂不支持）
     * @return 在线用户列表
     */
    List<OnlineUserVO> getOnlineUserList(String userName, String ipaddr);

    /**
     * 强制用户下线
     * <p>
     * 说明：通过删除 Redis 中的刷新令牌来实现强制下线。
     * 删除刷新令牌后，用户无法刷新 JWT Token，从而间接实现下线效果。
     * </p>
     *
     * @param sessionId 会话ID（实际为 Redis 中的刷新令牌 key，格式：jwt:refresh:user:{userId}）
     * @return 是否成功
     */
    boolean forceLogout(String sessionId);


    /**
     * 获取系统健康检查报告
     *
     * @return 健康检查报告
     */
    Map<String, Object> getHealthCheck();

    /**
     * 获取缓存列表（预定义的缓存组）
     *
     * @return 缓存列表
     */
    List<RedisCacheVO> getCacheList();

    /**
     * 根据缓存名称获取键名列表
     *
     * @param cacheName 缓存名称
     * @return 键名列表
     */
    List<RedisCacheVO.CacheKeyInfo> getCacheKeys(String cacheName);

    /**
     * 获取缓存内容
     *
     * @param cacheName 缓存名称
     * @param key 缓存键名
     * @return 缓存内容
     */
    RedisCacheVO.CacheContentInfo getCacheContent(String cacheName, String key);

    /**
     * 删除缓存（根据缓存名称删除所有匹配的键）
     *
     * @param cacheName 缓存名称
     * @return 删除的键数量
     */
    long deleteCache(String cacheName);

    /**
     * 删除单个缓存键
     *
     * @param cacheName 缓存名称
     * @param key 缓存键名
     * @return 是否成功
     */
    boolean deleteCacheKey(String cacheName, String key);

    /**
     * 清空所有缓存
     *
     * @return 是否成功
     */
    boolean clearAllCache();

    /**
     * 分页查询API性能监控数据
     *
     * @param reqBo 查询参数
     * @return 分页结果
     */
    PageResponse<SysMonitorApiPerformance> getApiPerformancePageList(ApiPerformanceReqBo reqBo);

    /**
     * 获取最慢的API接口（Top N）
     *
     * @param limit 查询数量限制
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return API性能数据列表
     */
    List<SysMonitorApiPerformance> getSlowestApis(Integer limit, LocalDate startDate, LocalDate endDate);

    /**
     * 获取错误率最高的API接口（Top N）
     *
     * @param limit 查询数量限制
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return API性能数据列表
     */
    List<SysMonitorApiPerformance> getHighestErrorRateApis(Integer limit, LocalDate startDate, LocalDate endDate);
}
