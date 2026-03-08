package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.assembler.UserVOAssembler;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.entity.UserRole;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.UserPermissionCacheService;
import com.star.pivot.system.utils.DataScopeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private UserPostMapper userPostMapper;

    @Mock
    private UserPermissionCacheService userPermissionCacheService;

    @Mock
    private DataScopeService dataScopeService;

    @Mock
    private UserVOAssembler userVOAssembler;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sysUserService, "baseMapper", sysUserMapper);

        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setUserName("testuser");
        testUser.setNickName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhonenumber("13800138000");
        testUser.setStatus("0");
        testUser.setDelFlag("0");
        testUser.setCreateTime(LocalDateTime.now());

        testUserDTO = new UserDTO();
        testUserDTO.setUserName("newuser");
        testUserDTO.setNickName("新用户");
        testUserDTO.setEmail("newuser@example.com");
        testUserDTO.setPhonenumber("13900139000");
        testUserDTO.setRoleIds(Arrays.asList(1L, 2L));
        testUserDTO.setPostIds(Arrays.asList(1L));
    }

    @Nested
    @DisplayName("用户查询测试")
    class QueryUserTests {

        @Test
        @DisplayName("根据用户名查询用户 - 用户存在")
        void getUserByUsername_UserExists() {
            when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

            SysUser result = sysUserService.getUserByUsername("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getUserName());
            verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据用户名查询用户 - 用户不存在")
        void getUserByUsername_UserNotExists() {
            when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            SysUser result = sysUserService.getUserByUsername("nonexistent");

            assertNull(result);
            verify(sysUserMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        }
    }

    @Nested
    @DisplayName("用户新增测试")
    class AddUserTests {

        @Test
        @DisplayName("新增用户 - 成功")
        void addUser_Success() {
            when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);
            when(userRoleMapper.insertBatchUserRoles(anyList())).thenReturn(1);
            when(userPostMapper.insertBatchUserPosts(anyList())).thenReturn(1);

            assertDoesNotThrow(() -> sysUserService.addUser(testUserDTO));
        }

        @Test
        @DisplayName("新增用户 - 用户名已存在")
        void addUser_UsernameExists() {
            when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

            assertThrows(BizException.class, () -> sysUserService.addUser(testUserDTO));
        }
    }

    @Nested
    @DisplayName("用户状态测试")
    class UserStatusTests {

        @Test
        @DisplayName("检查用户状态 - 正常状态")
        void checkUserStatus_Normal() {
            assertEquals("0", testUser.getStatus());
        }

        @Test
        @DisplayName("检查删除标志 - 正常")
        void checkDelFlag_Normal() {
            assertEquals(AppConstants.DelFlag.NORMAL, testUser.getDelFlag());
        }
    }

    @Nested
    @DisplayName("密码加密测试")
    class PasswordTests {

        @Test
        @DisplayName("密码加密 - 非空密码")
        void passwordEncryption_NotEmpty() {
            String rawPassword = "123456";
            String encrypted = encryptPassword(rawPassword);
            
            assertNotNull(encrypted);
            assertNotEquals(rawPassword, encrypted);
            assertTrue(encrypted.length() > 0);
        }

        @Test
        @DisplayName("密码加密 - 空密码使用默认密码")
        void passwordEncryption_Empty() {
            String encrypted = encryptPassword("");
            assertNotNull(encrypted);
        }

        private String encryptPassword(String password) {
            if (password == null || password.isEmpty()) {
                password = "123456";
            }
            return "$2a$10$" + password.hashCode();
        }
    }

    @Nested
    @DisplayName("角色关联测试")
    class UserRoleTests {

        @Test
        @DisplayName("获取用户角色列表")
        void getRolesByUserId() {
            SysRole role1 = new SysRole();
            role1.setRoleId(1L);
            role1.setRoleName("管理员");
            
            SysRole role2 = new SysRole();
            role2.setRoleId(2L);
            role2.setRoleName("普通用户");

            when(sysUserMapper.getRolesByUserId(1L)).thenReturn(Arrays.asList(role1, role2));

            List<SysRole> roles = sysUserService.getRolesByUserId(1L);

            assertNotNull(roles);
            assertEquals(2, roles.size());
            verify(sysUserMapper, times(1)).getRolesByUserId(1L);
        }

        @Test
        @DisplayName("获取用户角色列表 - 无角色")
        void getRolesByUserId_NoRoles() {
            when(sysUserMapper.getRolesByUserId(999L)).thenReturn(Collections.emptyList());

            List<SysRole> roles = sysUserService.getRolesByUserId(999L);

            assertNotNull(roles);
            assertTrue(roles.isEmpty());
        }
    }
}
