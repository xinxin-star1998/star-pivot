package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.system.domain.bo.OnlineUserVO;
import com.star.pivot.system.domain.entity.SysOnlineUser;
import com.star.pivot.system.mapper.SysOnlineUserMapper;
import com.star.pivot.system.service.interfaces.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
