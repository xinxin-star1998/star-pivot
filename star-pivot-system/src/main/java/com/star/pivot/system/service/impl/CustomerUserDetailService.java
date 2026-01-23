package com.star.pivot.system.service.impl;

import com.star.pivot.common.domain.Constants;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 * 
 * <p>性能优化：使用 Redis 缓存用户权限信息，减少数据库查询
 * <ul>
 *   <li>缓存 key: user:permissions:{username}</li>
 *   <li>缓存时间: 30分钟（可在配置文件中调整）</li>
 *   <li>当用户角色或权限变更时，需要调用 clearUserPermissionCache() 清除缓存</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {
    private final SysUserService userService;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper sysMenuMapper;
    
    /**
     * 加载用户详情（包含权限信息）
     * 
     * <p>使用缓存机制，避免每次请求都查询数据库
     * 缓存 key 为用户名，当用户权限变更时需要清除缓存
     * 
     * @param username 用户名
     * @return 用户详情（包含权限）
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    @Cacheable(cacheNames = "userPermissions", key = "#username", unless = "#result == null")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户权限信息（未命中缓存）: {}", username);
        
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 查询用户角色
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getUserId());
        
        // 检查用户是否拥有admin角色或dataScope=1的角色，如果有则查询所有权限菜单
        boolean isAdmin = roles.stream().anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()));
        // 检查是否有角色的dataScope为1（全部数据权限）
        boolean hasAllDataScope = roles.stream().anyMatch(role -> Constants.DataScope.ALL.equals(role.getDataScope()));
        
        List<SysMenu> permissions;
        if (isAdmin || hasAllDataScope) {
            // 获取所有菜单（用于构建树结构）
            permissions = sysMenuMapper.selectList(null);
            log.debug("Admin用户或拥有全部数据权限的用户，加载所有菜单权限: {}", username);
        } else {
            // 获取用户权限
            permissions = sysMenuMapper.selectPermissionsByUserId(user.getUserId());
            log.debug("普通用户，加载用户权限: {}, 权限数量: {}", username, permissions != null ? permissions.size() : 0);
        }

        // 确保 permissions 不为 null
        if (permissions == null) {
            permissions = new java.util.ArrayList<>();
        }

        // 构建权限列表，过滤掉null和空字符串的权限标识
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SysMenu::getPerms)
                .filter(perms -> perms != null && !perms.trim().isEmpty())
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