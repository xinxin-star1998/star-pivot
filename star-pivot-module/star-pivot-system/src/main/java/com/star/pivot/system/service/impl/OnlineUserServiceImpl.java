package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.utils.string.StringUtils;
import com.star.pivot.security.token.RefreshTokenManager;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.domain.entity.SysOnlineUser;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysOnlineUserMapper;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.service.interfaces.OnlineUserService;
import com.star.pivot.system.service.interfaces.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线用户历史记录服务实现类
 * <p>
 * 说明：用于保存在线用户的历史记录到数据库。
 * 实时数据存储在 Redis 中，当用户登出、强制下线或会话过期时，将记录保存到此服务。
 * </p>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Service
public class OnlineUserServiceImpl extends ServiceImpl<SysOnlineUserMapper, SysOnlineUser> implements OnlineUserService {

    @Autowired
    @Lazy
    private TokenService tokenService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RefreshTokenManager refreshTokenManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存在线用户历史记录
     * <p>
     * 说明：使用异步方式保存，避免影响主流程性能。
     * </p>
     *
     * @param onlineUser 在线用户信息（从 Redis 中获取）
     * @param logoutType 下线类型（0正常登出 1强制下线 2过期下线）
     */
    @Override
    @Async
    public void saveOnlineUserHistory(OnlineUserVO onlineUser, String logoutType) {
        if (onlineUser == null) {
            log.warn("在线用户信息为空，跳过保存历史记录");
            return;
        }

        try {
            SysOnlineUser sysOnlineUser = new SysOnlineUser();
            sysOnlineUser.setSessionId(onlineUser.getSessionId());
            sysOnlineUser.setUserId(onlineUser.getUserId());
            sysOnlineUser.setUserName(onlineUser.getUserName());
            sysOnlineUser.setNickName(onlineUser.getNickName());
            sysOnlineUser.setDeptName(onlineUser.getDeptName());
            sysOnlineUser.setIpaddr(onlineUser.getIpaddr());
            sysOnlineUser.setLoginLocation(onlineUser.getLoginLocation());
            sysOnlineUser.setBrowser(onlineUser.getBrowser());
            sysOnlineUser.setOs(onlineUser.getOs());
            sysOnlineUser.setStatus("1"); // 1表示离线
            sysOnlineUser.setStartTimestamp(onlineUser.getLoginTime());
            sysOnlineUser.setLastAccessTime(onlineUser.getLastAccessTime());
            sysOnlineUser.setEndTimestamp(LocalDateTime.now()); // 下线时间
            sysOnlineUser.setLogoutType(logoutType);
            sysOnlineUser.setCreateTime(LocalDateTime.now());

            // 保存或更新（如果已存在则更新）
            this.saveOrUpdate(sysOnlineUser);

            log.debug("已保存在线用户历史记录，sessionId: {}, logoutType: {}", onlineUser.getSessionId(), logoutType);
        } catch (Exception e) {
            log.error("保存在线用户历史记录失败，sessionId: {}", onlineUser.getSessionId(), e);
        }
    }

    @Override
    public PageResponse<OnlineUserVO> selectOnlineUserList(Long pageNum, Long pageSize, String userName, String ipaddr) {
        // 从Redis获取所有活跃的会话信息
        List<OnlineUserVO> allActiveSessions = getAllActiveSessionsFromRedis();

        // 过滤条件
        List<OnlineUserVO> filteredSessions = allActiveSessions.stream()
            .filter(session -> {
                boolean userNameMatch = StringUtils.isEmpty(userName) ||
                    (session.getUserName() != null && session.getUserName().contains(userName));
                boolean ipMatch = StringUtils.isEmpty(ipaddr) ||
                    (session.getIpaddr() != null && session.getIpaddr().contains(ipaddr));
                return userNameMatch && ipMatch;
            })
            .collect(Collectors.toList());

        // 分页处理
        int startIndex = Math.toIntExact((pageNum - 1) * pageSize);
        int endIndex = Math.toIntExact(Math.min(startIndex + pageSize, filteredSessions.size()));

        List<OnlineUserVO> pageSessions = new ArrayList<>();
        if (startIndex < filteredSessions.size()) {
            pageSessions = filteredSessions.subList(startIndex, endIndex);
        }

        PageResponse<OnlineUserVO> response = new PageResponse<>();
        response.setTotal(Long.valueOf(filteredSessions.size()));
        response.setRows(pageSessions);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);

        return response;
    }

    @Override
    public boolean forceLogout(String sessionId) {
        try {
            // 从sessionId解析userId，格式: jwt:refresh:user:{userId}
            Long userId = extractUserIdFromSessionId(sessionId);
            if (userId != null) {
                tokenService.forceLogout(userId, "1"); // 1表示强制下线
                return true;
            }

            log.warn("无法从sessionId提取userId: {}", sessionId);
            return false;
        } catch (Exception e) {
            log.error("强制用户下线失败: sessionId={}", sessionId, e);
            return false;
        }
    }

    @Override
    public boolean batchForceLogout(List<String> sessionIds) {
        boolean allSuccess = true;
        for (String sessionId : sessionIds) {
            boolean success = forceLogout(sessionId);
            if (!success) {
                allSuccess = false;
                log.warn("批量强制下线中的某个会话失败: {}", sessionId);
            }
        }
        return allSuccess;
    }

    @Override
    public void forceLogoutByUserId(Long userId) {
        tokenService.forceLogout(userId, "1"); // 1表示强制下线
    }

    /**
     * 从Redis获取所有活跃会话
     */
    private List<OnlineUserVO> getAllActiveSessionsFromRedis() {
        List<OnlineUserVO> sessions = new ArrayList<>();

        try {
            // 使用Redis的SCAN命令获取所有以"jwt:refresh:user:"开头的键
            Set<String> refreshKeys = redisTemplate.keys("jwt:refresh:user:*");

            if (refreshKeys != null) {
                for (String key : refreshKeys) {
                    try {
                        // 从key中提取userId
                        String userIdStr = extractUserIdFromRefreshTokenKey(key);
                        if (userIdStr != null) {
                            Long userId = Long.parseLong(userIdStr);

                            // 获取用户信息
                            SysUser user = sysUserMapper.selectById(userId);
                            if (user != null) {
                                // 获取该用户的活跃会话
                                List<RefreshTokenManager.DeviceSessionInfo> userSessions =
                                    refreshTokenManager.getUserActiveSessions(userId);

                                // 创建在线用户信息
                                for (RefreshTokenManager.DeviceSessionInfo session : userSessions) {
                                    OnlineUserVO vo = new OnlineUserVO();
                                    vo.setSessionId(session.getDeviceSessionId());
                                    vo.setUserId(userId);
                                    vo.setUserName(user.getUserName());
                                    vo.setNickName(user.getNickName());

                                    // 从Redis获取部门信息
                                    if (user.getDeptId() != null) {
                                        // 这里需要从其他地方获取部门名称
                                    }

                                    vo.setIpaddr(session.getIpaddr());
                                    vo.setBrowser(session.getBrowser());
                                    vo.setOs(session.getOs());

                                    if (session.getCreatedAt() != null) {
                                        vo.setLoginTime(session.getCreatedAt().toInstant()
                                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                                    }
                                    if (session.getLastAccessTime() != null) {
                                        vo.setLastAccessTime(session.getLastAccessTime().toInstant()
                                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                                    }

                                    sessions.add(vo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("处理用户会话信息时出错: key={}", key, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所有活跃会话失败", e);
        }

        return sessions;
    }

    /**
     * 从刷新令牌键中提取用户ID
     */
    private String extractUserIdFromRefreshTokenKey(String key) {
        if (key != null && key.startsWith("jwt:refresh:user:")) {
            return key.substring("jwt:refresh:user:".length());
        }
        return null;
    }

    /**
     * 从sessionId提取userId
     */
    private Long extractUserIdFromSessionId(String sessionId) {
        if (sessionId != null && sessionId.startsWith("jwt:refresh:user:")) {
            try {
                String userIdStr = sessionId.substring("jwt:refresh:user:".length());
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.warn("无法解析sessionId中的userId: {}", sessionId);
                return null;
            }
        }
        return null;
    }
}
