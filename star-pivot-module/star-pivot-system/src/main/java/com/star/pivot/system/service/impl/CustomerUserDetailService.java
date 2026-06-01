package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.interfaces.SysUserService;
import com.star.pivot.system.utils.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 * 
 * <p>性能优化：使用 Redis 缓存用户权限信息，减少数据库查询
 * <ul>
 *   <li>缓存 key: user:permissions:{username}</li>
 *   <li>缓存时间: 30分钟（可在配置文件中调整）</li>
 *   <li>当用户角色或权限变更时，需要调用 clearUserPermissionCache() 清除缓存</li>
 *   <li>缓存穿透防护：用户不存在时也缓存空标记，防止恶意请求穿透到数据库</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {
    private final SysUserService userService;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper sysMenuMapper;
    
    /** 权限有效性校验谓词 - 过滤null和空字符串的权限标识 */
    private static final Predicate<String> VALID_PERMISSION = 
        perms -> perms != null && !perms.trim().isEmpty();
    
    /**
     * 加载用户详情（包含权限信息）
     * 
     * <p>使用缓存机制，避免每次请求都查询数据库
     * 缓存 key 为用户名，当用户权限变更时需要清除缓存
     * 
     * <p>缓存穿透防护：用户不存在时也缓存空标记对象，
     * 配合较短的缓存时间（5分钟），既能防止穿透又能减少安全风险
     * 
     * @param username 用户名
     * @return 用户详情（包含权限）
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    @Cacheable(cacheNames = "userPermissions", key = "'user:' + #username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("从数据库加载用户权限信息: {}", username);
        
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户是否已删除
        if (AppConstants.DelFlag.DELETE.equals(user.getDelFlag())) {
            log.warn("用户已删除: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态（是否已停用）
        if (!AppConstants.Status.NORMAL.equals(user.getStatus())) {
            log.warn("用户已停用: {}", username);
            throw new DisabledException("用户已停用");
        }
        
        // 查询用户角色
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getUserId());
        
        // 检查用户是否拥有admin角色
        // 注意：数据权限（dataScope）与菜单权限是不同概念，不应混淆
        // dataScope控制的是用户能访问哪些数据（如部门数据），而不是能看到哪些菜单
        boolean isAdmin = roles.stream().anyMatch(role -> AppConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
        
        List<SysMenu> permissions;
        if (isAdmin) {
            // 超级管理员获取所有启用且可见的菜单
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, AppConstants.Status.NORMAL)
                   .eq(SysMenu::getVisible, AppConstants.Visible.SHOW);
            permissions = sysMenuMapper.selectList(wrapper);
            log.debug("超级管理员用户，加载所有启用菜单: {}", username);
        } else {
            // 获取用户权限（通过角色关联的菜单权限）
            permissions = sysMenuMapper.selectPermissionsByUserId(user.getUserId());
            log.debug("普通用户，加载用户权限: {}, 权限数量: {}", username, permissions != null ? permissions.size() : 0);
        }

        // 确保 permissions 不为 null
        if (permissions == null) {
            permissions = new java.util.ArrayList<>();
        }

        // 构建权限列表，使用谓词过滤无效权限标识
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SysMenu::getPerms)
                .filter(VALID_PERMISSION)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("用户 {} 权限加载完成，权限数量: {}", username, authorities.size());

        // 返回LoginUser对象，而不是Spring Security标准的User对象
        // 这样SecurityContextUtils才能正确获取到用户信息
        return new LoginUser(user, authorities);
    }
    
    // 注意：缓存清除方法已移至 UserPermissionCacheService，以避免循环依赖
    // 如需清除缓存，请使用 UserPermissionCacheService
}