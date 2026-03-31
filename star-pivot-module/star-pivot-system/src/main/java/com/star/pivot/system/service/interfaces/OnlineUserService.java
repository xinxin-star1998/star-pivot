package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.bo.OnlineUserVO;

/**
 * 在线用户历史记录服务接口
 * <p>
 * 说明：用于保存在线用户的历史记录到数据库。
 * 实时数据存储在 Redis 中，当用户登出、强制下线或会话过期时，将记录保存到此服务。
 * </p>
 *
 * @author xinxin
 * @since 2026-01-25
 */
public interface OnlineUserService {

    /**
     * 保存在线用户历史记录
     * <p>
     * 说明：当用户登出、强制下线或会话过期时调用此方法保存历史记录。
     * 使用异步方式保存，避免影响主流程性能。
     * </p>
     *
     * @param onlineUser 在线用户信息（从 Redis 中获取）
     * @param logoutType 下线类型（0正常登出 1强制下线 2过期下线）
     */
    void saveOnlineUserHistory(OnlineUserVO onlineUser, String logoutType);
}
