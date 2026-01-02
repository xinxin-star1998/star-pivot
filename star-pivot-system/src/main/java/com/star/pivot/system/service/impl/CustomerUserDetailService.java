package com.star.pivot.system.service.impl;
import com.star.pivot.common.constants.Constants;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerUserDetailService implements UserDetailsService {
    private final SysUserService userService;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper sysMenuMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 查询用户角色
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getUserId());
        //遍历roles，如果roleKey === admin ,查询所有权限菜单
        List<SysMenu> permissions = null;
        if (roles.stream().anyMatch(role -> Constants.ADMIN_ROLE_KEY.equals(role.getRoleKey()))) {
            // 获取所有菜单（用于构建树结构）
            permissions = sysMenuMapper.selectList(null);
        }else{
            // 获取用户权限
            permissions = sysMenuMapper.selectPermissionsByUserId(user.getUserId());
        }
        // 查询用户权限
//        List<SysMenu> permissions = sysMenuMapper.selectPermissionsByUserId(user.getUserId());

        // 构建权限列表，过滤掉null和空字符串的权限标识
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SysMenu::getPerms)
                .filter(perms -> perms != null && !perms.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 判断账号是否启用（status为"0"表示正常，"1"表示停用）
        boolean enabled = user.getStatus() != null && "0".equals(user.getStatus());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                enabled,  // 账号是否启用
                true,     // 账号是否未过期
                true,     // 凭证是否未过期
                true,     // 账号是否未锁定
                authorities
        );
    }
}