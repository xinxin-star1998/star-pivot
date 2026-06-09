package com.star.pivot.system.service;

import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.interfaces.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * PermissionService 单元测试
 *
 * @author xinxin
 * @since 2026-06-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceTest {

    @Mock
    private SysMenuMapper menuMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @InjectMocks
    private PermissionService permissionService;

    private List<SysMenu> testMenus;
    private List<SysRole> testRoles;

    @BeforeEach
    void setUp() {
        // 准备测试菜单数据
        SysMenu menu1 = new SysMenu();
        menu1.setMenuId(1L);
        menu1.setPerms("system:user:query");
        menu1.setMenuType("F"); // 按钮

        SysMenu menu2 = new SysMenu();
        menu2.setMenuId(2L);
        menu2.setPerms("system:user:add");
        menu2.setMenuType("F");

        testMenus = Arrays.asList(menu1, menu2);

        // 准备测试角色数据
        SysRole role1 = new SysRole();
        role1.setRoleId(1L);
        role1.setRoleKey("admin");

        SysRole role2 = new SysRole();
        role2.setRoleId(2L);
        role2.setRoleKey("common");

        testRoles = Arrays.asList(role1, role2);
    }

    @Test
    @DisplayName("测试获取用户权限标识集合")
    void testGetMenuPermission() {
        // Given
        when(menuMapper.selectMenuPermsByUserId(anyLong())).thenReturn(
                Arrays.asList("system:user:query", "system:user:add", "system:role:query")
        );

        // When
        Set<String> permissions = permissionService.getMenuPermission(1L);

        // Then
        assertNotNull(permissions);
        assertEquals(3, permissions.size());
        assertTrue(permissions.contains("system:user:query"));
        assertTrue(permissions.contains("system:user:add"));
        assertTrue(permissions.contains("system:role:query"));
    }

    @Test
    @DisplayName("测试获取用户角色集合")
    void testGetRolePermission() {
        // Given
        when(roleMapper.selectRolePermsByUserId(anyLong())).thenReturn(
                Arrays.asList("admin", "common")
        );

        // When
        Set<String> roles = permissionService.getRolePermission(1L);

        // Then
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("admin"));
        assertTrue(roles.contains("common"));
    }

    @Test
    @DisplayName("测试验证用户是否包含权限 - 包含")
    void testHasPermissions_True() {
        // Given
        Set<String> permissions = new HashSet<>(Arrays.asList("system:user:query", "system:user:add"));

        // When
        boolean hasPermission = permissionService.hasPermissions(permissions, "system:user:query");

        // Then
        assertTrue(hasPermission);
    }

    @Test
    @DisplayName("测试验证用户是否包含权限 - 不包含")
    void testHasPermissions_False() {
        // Given
        Set<String> permissions = new HashSet<>(Arrays.asList("system:user:query"));

        // When
        boolean hasPermission = permissionService.hasPermissions(permissions, "system:role:add");

        // Then
        assertFalse(hasPermission);
    }

    @Test
    @DisplayName("测试验证用户是否包含角色 - 包含")
    void testHasRole_True() {
        // Given
        Set<String> roles = new HashSet<>(Arrays.asList("admin", "common"));

        // When
        boolean hasRole = permissionService.hasRole(roles, "admin");

        // Then
        assertTrue(hasRole);
    }

    @Test
    @DisplayName("测试验证用户是否包含角色 - 不包含")
    void testHasRole_False() {
        // Given
        Set<String> roles = new HashSet<>(Arrays.asList("common"));

        // When
        boolean hasRole = permissionService.hasRole(roles, "admin");

        // Then
        assertFalse(hasRole);
    }

    @Test
    @DisplayName("测试空权限集合")
    void testEmptyPermissions() {
        // Given
        when(menuMapper.selectMenuPermsByUserId(anyLong())).thenReturn(Arrays.asList());

        // When
        Set<String> permissions = permissionService.getMenuPermission(999L);

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("测试空角色集合")
    void testEmptyRoles() {
        // Given
        when(roleMapper.selectRolePermsByUserId(anyLong())).thenReturn(Arrays.asList());

        // When
        Set<String> roles = permissionService.getRolePermission(999L);

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }
}
