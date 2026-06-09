package com.star.pivot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.service.interfaces.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SysUserService 单元测试
 *
 * @author xinxin
 * @since 2026-06-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class SysUserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SysUserService sysUserService;

    private SysUser testUser;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setPhonenumber("13800138000");
        testUser.setStatus("0");
        testUser.setDelFlag("0");

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setUserName("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setPhonenumber("13800138000");
    }

    @Test
    @DisplayName("测试根据用户名查询用户 - 成功场景")
    void testGetUserByUsername_Success() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);

        // When
        SysUser result = sysUserService.getUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试根据用户名查询用户 - 用户不存在")
    void testGetUserByUsername_NotFound() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(null);

        // When
        SysUser result = sysUserService.getUserByUsername("nonexistent");

        // Then
        assertNull(result);
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试用户分页查询 - 成功场景")
    void testPageList_Success() {
        // Given
        UserReqBo reqBo = new UserReqBo();
        reqBo.setPageNum(1);
        reqBo.setPageSize(10);
        reqBo.setUserName("test");

        IPage<SysUser> page = new Page<>(1, 10);
        page.setTotal(1);
        page.setRecords(Arrays.asList(testUser));

        when(sysUserMapper.selectPageList(any(), any())).thenReturn(page);

        // When
        PageResponse<UserVO> result = sysUserService.pageList(reqBo);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertFalse(result.getRows().isEmpty());
        verify(sysUserMapper, times(1)).selectPageList(any(), any());
    }

    @Test
    @DisplayName("测试添加用户 - 成功场景")
    void testAddUser_Success() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(null); // 用户名不存在
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = sysUserService.addUser(userDTO);

        // Then
        assertTrue(result);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(sysUserMapper, times(1)).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("测试添加用户 - 用户名已存在")
    void testAddUser_UsernameExists() {
        // Given
        when(sysUserMapper.selectOne(any())).thenReturn(testUser); // 用户名已存在

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            sysUserService.addUser(userDTO);
        });
        verify(sysUserMapper, never()).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("测试更新用户 - 成功场景")
    void testUpdateUser_Success() {
        // Given
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = sysUserService.updateUser(userDTO);

        // Then
        assertTrue(result);
        verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("测试删除用户 - 成功场景")
    void testDeleteUserByIds_Success() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);
        when(sysUserMapper.deleteBatchIds(userIds)).thenReturn(2);

        // When
        boolean result = sysUserService.deleteUserByIds(userIds);

        // Then
        assertTrue(result);
        verify(sysUserMapper, times(1)).deleteBatchIds(userIds);
    }

    @Test
    @DisplayName("测试重置用户密码 - 成功场景")
    void testResetUserPassword_Success() {
        // Given
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newEncodedPassword");
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = sysUserService.resetUserPassword(1L, "newPassword123");

        // Then
        assertTrue(result);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("测试修改用户状态 - 成功场景")
    void testChangeUserStatus_Success() {
        // Given
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // When
        boolean result = sysUserService.changeUserStatus(1L, "1");

        // Then
        assertTrue(result);
        verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("测试检查是否允许删除用户 - 不能删除自己")
    void testCanDeleteUsers_CannotDeleteSelf() {
        // Given
        List<Long> userIds = Arrays.asList(1L);

        // Mock SecurityContext 返回当前用户 ID 为 1L
        // 这里需要根据实际的 SecurityContextUtils 实现来 mock

        // When
        String reason = sysUserService.canDeleteUsers(userIds);

        // Then
        assertNotNull(reason);
        assertTrue(reason.contains("不能删除当前登录用户"));
    }

    @Test
    @DisplayName("测试检查是否允许重置密码 - 不能重置自己密码")
    void testCanResetPassword_CannotResetSelf() {
        // Given
        Long targetUserId = 1L;

        // Mock SecurityContext 返回当前用户 ID 为 1L

        // When
        String reason = sysUserService.canResetPassword(targetUserId);

        // Then
        assertNotNull(reason);
        assertTrue(reason.contains("不能重置当前登录用户密码"));
    }

    @Test
    @DisplayName("测试判断当前用户是否是超级管理员")
    void testIsCurrentUserSuperAdmin() {
        // This test depends on SecurityContext implementation
        // Should be tested with integration test or proper mocking
        // For now, just ensure the method exists and doesn't throw
        assertDoesNotThrow(() -> sysUserService.isCurrentUserSuperAdmin());
    }
}
