package com.star.pivot.system.service.impl;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.security.token.RefreshTokenManager;
import com.star.pivot.security.token.RefreshTokenManager.DeviceSessionInfo;
import com.star.pivot.system.domain.bo.DeviceSessionVO;
import com.star.pivot.system.service.interfaces.SessionManagementService;
import com.star.pivot.system.service.interfaces.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManagementServiceImpl implements SessionManagementService {
    
    private final RefreshTokenManager refreshTokenManager;
    private final SysUserService sysUserService;
    
    @Override
    public List<DeviceSessionVO> getUserActiveSessions(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "用户ID不能为空");
        }
        
        if (!canManageSession(userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权查看该用户的会话信息");
        }
        
        List<DeviceSessionInfo> sessions = refreshTokenManager.getUserActiveSessions(userId);
        log.debug("用户 {} 的活跃会话数: {}", userId, sessions.size());
        
        // 获取当前用户的设备会话 ID
        Long currentUserId = SecurityContextUtils.getUserId();
        String currentDeviceSessionId = null;
        if (currentUserId != null && currentUserId.equals(userId)) {
            RefreshTokenManager.RefreshTokenValue currentValue = refreshTokenManager.getRefreshTokenValue(currentUserId);
            if (currentValue != null) {
                currentDeviceSessionId = currentValue.getDeviceSessionId();
                log.debug("当前用户 {} 的设备会话ID: {}", currentUserId, currentDeviceSessionId);
            }
        }
        
        final String finalCurrentDeviceSessionId = currentDeviceSessionId;
        
        return sessions.stream()
            .map(sessionInfo -> convertToVO(sessionInfo, finalCurrentDeviceSessionId))
            // 当前会话置顶，其余按最后登录时间倒序
            .sorted(Comparator
                .<DeviceSessionVO, Boolean>comparing(vo -> !vo.isCurrent())
                .thenComparing(
                    Comparator.comparing(DeviceSessionVO::getLastAccessTime, Comparator.nullsLast(Comparator.reverseOrder()))
                ))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean forceLogoutSession(Long userId, String deviceSessionId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "用户ID不能为空");
        }
        if (deviceSessionId == null || deviceSessionId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "设备会话ID不能为空");
        }
        
        if (!canManageSession(userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该用户的会话");
        }
        
        try {
            // 只删除指定的设备会话，不影响其他设备
            refreshTokenManager.removeDeviceSession(userId, deviceSessionId);
            
            log.info("已强制下线用户 {} 的会话: {}", userId, deviceSessionId);
            return true;
        } catch (Exception e) {
            log.error("强制下线会话失败，userId={}, deviceSessionId={}", userId, deviceSessionId, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "强制下线失败");
        }
    }
    
    @Override
    public void forceLogoutAllSessions(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "用户ID不能为空");
        }
        
        if (!canManageSession(userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该用户的会话");
        }
        
        try {
            refreshTokenManager.revokeAllUserSessions(userId);
            log.info("已强制下线用户 {} 的所有会话", userId);
        } catch (Exception e) {
            log.error("强制下线用户所有会话失败，userId={}", userId, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "强制下线失败");
        }
    }
    
    @Override
    public boolean canManageSession(Long targetUserId) {
        if (targetUserId == null) {
            return false;
        }
        
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        
        if (currentUserId.equals(targetUserId)) {
            return true;
        }
        
        return sysUserService.isCurrentUserSuperAdmin();
    }
    
    private DeviceSessionVO convertToVO(DeviceSessionInfo sessionInfo, String currentDeviceSessionId) {
        DeviceSessionVO vo = new DeviceSessionVO();
        vo.setDeviceSessionId(sessionInfo.getDeviceSessionId());
        vo.setIpaddr(sessionInfo.getIpaddr());
        vo.setBrowser(sessionInfo.getBrowser());
        vo.setOs(sessionInfo.getOs());
        vo.setCreatedAt(sessionInfo.getCreatedAt());
        vo.setLastAccessTime(sessionInfo.getLastAccessTime());
        
        // 标记当前会话
        vo.setCurrent(currentDeviceSessionId != null && currentDeviceSessionId.equals(sessionInfo.getDeviceSessionId()));
        
        if (sessionInfo.getCreatedAt() != null && sessionInfo.getLastAccessTime() != null) {
            long duration = sessionInfo.getLastAccessTime().getTime() - sessionInfo.getCreatedAt().getTime();
            vo.setSessionDuration(formatDuration(duration));
        }
        
        return vo;
    }
    
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d天%d小时", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟", minutes);
        } else {
            return String.format("%d秒", seconds);
        }
    }
}
