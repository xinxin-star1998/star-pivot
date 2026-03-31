package com.star.pivot.system.service;

import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.domain.entity.SysRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务类
 * 
 * <p>用于在 {@code @PreAuthorize} 注解中提供权限检查方法
 * <p>通过 {@code @PreAuthorize("@ss.hasRole('admin')")} 的方式使用
 * 
 * @author xinxin
 * @since 2026-01-23
 */
@Slf4j
@Service("ss")
@RequiredArgsConstructor
public class PermissionService {
    
    private final SysUserService sysUserService;
    
    /**
     * 检查当前登录用户是否拥有指定角色
     * 
     * @param roleKey 角色标识（如 "admin"）
     * @return 如果当前用户拥有该角色返回 true，否则返回 false
     */
    public boolean hasRole(String roleKey) {
        // 获取当前登录用户ID
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            log.debug("当前用户未登录，hasRole('{}') 返回 false", roleKey);
            return false;
        }
        
        try {
            // 查询当前用户的所有角色
            List<SysRole> roles = sysUserService.getRolesByUserId(userId);
            if (roles == null || roles.isEmpty()) {
                log.debug("用户 {} 没有角色，hasRole('{}') 返回 false", userId, roleKey);
                return false;
            }
            
            // 检查是否包含指定角色
            boolean hasRole = roles.stream()
                    .anyMatch(role -> roleKey.equals(role.getRoleKey()));
            
            log.debug("用户 {} 检查角色 '{}': {}", userId, roleKey, hasRole);
            return hasRole;
        } catch (Exception e) {
            log.error("检查用户角色时发生异常，用户ID: {}, 角色: {}", userId, roleKey, e);
            return false;
        }
    }
    
    /**
     * 检查当前登录用户是否拥有指定权限
     * 
     * <p>注意：此方法用于兼容性，实际权限检查建议使用 {@code hasAuthority} 表达式
     * 
     * @param permission 权限标识（如 "system:user:add"）
     * @return 如果当前用户拥有该权限返回 true，否则返回 false
     */
    public boolean hasPermission(String permission) {
        // 获取当前登录用户ID
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            log.debug("当前用户未登录，hasPermission('{}') 返回 false", permission);
            return false;
        }
        
        try {
            // 查询当前用户的所有权限
            List<String> permissions = sysUserService.getMenuByUserId(userId)
                    .stream()
                    .map(menu -> menu.getPerms())
                    .filter(perms -> perms != null && !perms.trim().isEmpty())
                    .toList();
            
            boolean hasPerm = permissions.contains(permission);
            log.debug("用户 {} 检查权限 '{}': {}", userId, permission, hasPerm);
            return hasPerm;
        } catch (Exception e) {
            log.error("检查用户权限时发生异常，用户ID: {}, 权限: {}", userId, permission, e);
            return false;
        }
    }
}
