package com.star.pivot.system.integration;

import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.RoleDeptMapper;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.utils.DataScopeService;
import com.star.pivot.system.utils.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * 数据权限服务集成测试
 */
@ExtendWith(MockitoExtension.class)
class DataScopeServiceIntegrationTest {

    @Mock
    private SysRoleMapper roleMapper;
    @Mock
    private SysUserMapper userMapper;
    @Mock
    private SysDeptMapper deptMapper;
    @Mock
    private RoleDeptMapper roleDeptMapper;

    @InjectMocks
    private DataScopeService dataScopeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserDataScope_shouldReturnNoDataWhenNotLoggedIn() {
        DataScope scope = dataScopeService.getCurrentUserDataScope();

        assertEquals("EMPTY_IN", scope.getSqlFilter());
        assertTrue(scope.getDeptIds().isEmpty());
    }

    @Test
    void getCurrentUserDataScope_shouldReturnAllForSuperAdmin() {
        loginAs(AppConstants.ADMIN_USER_ID);

        SysUser admin = new SysUser();
        admin.setUserId(AppConstants.ADMIN_USER_ID);
        admin.setDeptId(100L);
        when(userMapper.selectById(AppConstants.ADMIN_USER_ID)).thenReturn(admin);

        SysRole adminRole = new SysRole();
        adminRole.setRoleKey(AppConstants.ADMIN_ROLE_KEY);
        adminRole.setDataScope(AppConstants.DataScope.ALL);
        when(roleMapper.selectRolesByUserId(AppConstants.ADMIN_USER_ID)).thenReturn(List.of(adminRole));

        DataScope scope = dataScopeService.getCurrentUserDataScope();

        assertEquals("1=1", scope.getSqlFilter());
        assertEquals(AppConstants.ADMIN_USER_ID, scope.getUserId());
    }

    @Test
    void getCurrentUserDataScope_shouldReturnSelfScopeForNormalUser() {
        loginAs(2L);

        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setDeptId(200L);
        when(userMapper.selectById(2L)).thenReturn(user);

        SysRole role = new SysRole();
        role.setRoleId(5L);
        role.setDataScope(AppConstants.DataScope.SELF);
        when(roleMapper.selectRolesByUserId(2L)).thenReturn(List.of(role));

        DataScope scope = dataScopeService.getCurrentUserDataScope();

        assertEquals("u.user_id = #{param.userId}", scope.getSqlFilter());
        assertEquals(2L, scope.getUserId());
    }

    @Test
    void getCurrentUserDataScope_shouldReturnDeptScope() {
        loginAs(3L);

        SysUser user = new SysUser();
        user.setUserId(3L);
        user.setDeptId(300L);
        when(userMapper.selectById(3L)).thenReturn(user);

        SysRole role = new SysRole();
        role.setRoleId(6L);
        role.setDataScope(AppConstants.DataScope.DEPT);
        when(roleMapper.selectRolesByUserId(3L)).thenReturn(List.of(role));

        DataScope scope = dataScopeService.getCurrentUserDataScope();

        assertEquals("u.dept_id = #{param.userDeptId}", scope.getSqlFilter());
        assertEquals(300L, scope.getUserDeptId());
    }

    @Test
    void getCurrentUserDataScope_shouldReturnCustomDeptIds() {
        loginAs(4L);

        SysUser user = new SysUser();
        user.setUserId(4L);
        user.setDeptId(400L);
        when(userMapper.selectById(4L)).thenReturn(user);

        SysRole role = new SysRole();
        role.setRoleId(7L);
        role.setDataScope(AppConstants.DataScope.CUSTOM);
        when(roleMapper.selectRolesByUserId(4L)).thenReturn(List.of(role));
        when(roleDeptMapper.selectDeptIdsByRoleId(7L)).thenReturn(List.of(10L, 20L));

        DataScope scope = dataScopeService.getCurrentUserDataScope();

        assertTrue(scope.getSqlFilter().contains("u.dept_id IN ("));
        assertTrue(scope.getSqlFilter().contains("10"));
        assertTrue(scope.getSqlFilter().contains("20"));
        assertEquals(2, scope.getDeptIds().size());
        assertTrue(scope.getDeptIds().containsAll(List.of(10L, 20L)));
    }

    private void loginAs(Long userId) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        SysUser user = new SysUser();
        user.setUserId(userId);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList())
        );
    }
}
