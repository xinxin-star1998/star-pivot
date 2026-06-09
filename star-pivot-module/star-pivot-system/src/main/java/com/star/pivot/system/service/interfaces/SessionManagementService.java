package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.bo.DeviceSessionVO;

import java.util.List;

/**
 * 会话管理服务接口
 * 
 * <p>提供用户会话管理功能，包括：
 * <ul>
 *   <li>查询用户活跃会话列表</li>
 *   <li>强制下线指定会话</li>
 *   <li>强制下线用户所有会话</li>
 * </ul>
 */
public interface SessionManagementService {
    
    /**
     * 获取用户活跃会话列表
     * 
     * @param userId 用户ID
     * @return 活跃会话列表
     */
    List<DeviceSessionVO> getUserActiveSessions(Long userId);
    
    /**
     * 强制下线指定会话
     * 
     * @param userId 用户ID
     * @param deviceSessionId 设备会话ID
     * @return 是否成功
     */
    boolean forceLogoutSession(Long userId, String deviceSessionId);
    
    /**
     * 强制下线用户所有会话
     * 
     * @param userId 用户ID
     */
    void forceLogoutAllSessions(Long userId);
    
    /**
     * 检查是否为当前用户的会话（权限校验）
     * 
     * @param targetUserId 目标用户ID
     * @return 是否有权限操作
     */
    boolean canManageSession(Long targetUserId);
}
