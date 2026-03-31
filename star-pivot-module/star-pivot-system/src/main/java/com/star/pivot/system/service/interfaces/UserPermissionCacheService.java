package com.star.pivot.system.service.interfaces;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 用户权限缓存服务
 * 
 * <p>专门用于管理用户权限缓存的服务，解决循环依赖问题
 * 
 * <p>职责：
 * <ul>
 *   <li>清除指定用户的权限缓存</li>
 *   <li>清除所有用户的权限缓存</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>用户角色变更时，清除该用户的权限缓存</li>
 *   <li>角色权限分配变更时，清除所有用户权限缓存</li>
 *   <li>菜单权限变更时，清除所有用户权限缓存</li>
 * </ul>
 */
@Service
public class UserPermissionCacheService {

    /**
     * 清除指定用户的权限缓存
     * 
     * @param username 用户名
     */
    @CacheEvict(cacheNames = "userPermissions", key = "#username")
    public void clearUserPermissionCache(String username) {
        // 通过 @CacheEvict 注解自动清除缓存
        // 无需额外实现，Spring Cache 会自动处理
    }

    /**
     * 清除所有用户的权限缓存
     * 
     * <p>当系统权限配置发生全局变更时调用
     */
    @CacheEvict(cacheNames = "userPermissions", allEntries = true)
    public void clearAllUserPermissionCache() {
        // 通过 @CacheEvict 注解自动清除缓存
        // 无需额外实现，Spring Cache 会自动处理
    }
}
