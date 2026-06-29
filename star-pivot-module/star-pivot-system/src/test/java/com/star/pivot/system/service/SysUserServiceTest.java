package com.star.pivot.system.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.security.context.SecurityUtils;
import com.star.pivot.system.assembler.UserVOAssembler;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.mapper.UserRoleMapper;
import com.star.pivot.system.service.impl.SysUserServiceImpl;
import com.star.pivot.system.service.interfaces.TokenService;
import com.star.pivot.system.service.interfaces.UserPermissionCacheService;
import com.star.pivot.system.utils.DataScopeService;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class SysUserServiceTest {

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
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;
    private UserDTO userDTO;

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SysUser.class);
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sysUserService, "baseMapper", sysUserMapper);

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
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);

        SysUser result = sysUserService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试根据用户名查询用户 - 用户不存在")
    void testGetUserByUsername_NotFound() {
        when(sysUserMapper.selectOne(any())).thenReturn(null);

        SysUser result = sysUserService.getUserByUsername("nonexistent");

        assertNull(result);
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试用户分页查询 - 成功场景")
    void testPageList_Success() {
        UserReqBo reqBo = new UserReqBo();
        reqBo.setPageNum(1);
        reqBo.setPageSize(10);
        reqBo.setUserName("test");

        DataScope dataScope = new DataScope("1=1", Collections.emptyList(), 1L, 100L);
        when(dataScopeService.getCurrentUserDataScope()).thenReturn(dataScope);

        IPage<SysUser> page = new Page<>(1, 10);
        page.setTotal(1);
        page.setRecords(Arrays.asList(testUser));

        UserVO userVO = new UserVO();
        userVO.setUserName("testuser");
        when(userVOAssembler.convertToVOList(any())).thenReturn(Arrays.asList(userVO));
        when(sysUserMapper.selectPageList(any(), any())).thenReturn(page);

        PageResponse<UserVO> result = sysUserService.pageList(reqBo);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertFalse(result.getRows().isEmpty());
        verify(sysUserMapper, times(1)).selectPageList(any(), any());
    }

    @Test
    @DisplayName("测试添加用户 - 成功场景")
    void testAddUser_Success() {
        try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class);
             MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {

            secUtils.when(() -> SecurityUtils.encryptPassword(anyString())).thenReturn("$2a$10$encodedPassword");
            secCtx.when(SecurityContextUtils::getUsername).thenReturn("admin");

            when(sysUserMapper.selectOne(any())).thenReturn(null);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

            boolean result = sysUserService.addUser(userDTO);

            assertTrue(result);
            secUtils.verify(() -> SecurityUtils.encryptPassword("password123"));
            verify(sysUserMapper, times(1)).insert(any(SysUser.class));
        }
    }

    @Test
    @DisplayName("测试添加用户 - 用户名已存在")
    void testAddUser_UsernameExists() {
        when(sysUserMapper.selectOne(any())).thenReturn(testUser);

        assertThrows(BizException.class, () -> sysUserService.addUser(userDTO));
        verify(sysUserMapper, never()).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("测试更新用户 - 成功场景")
    void testUpdateUser_Success() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUsername).thenReturn("admin");

            when(sysUserMapper.selectById(1L)).thenReturn(testUser);
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            boolean result = sysUserService.updateUser(userDTO);

            assertTrue(result);
            verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
        }
    }

    @Test
    @DisplayName("测试删除用户 - 成功场景（软删除）")
    void testDeleteUserByIds_Success() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUsername).thenReturn("admin");

            List<Long> userIds = Arrays.asList(1L, 2L);
            when(sysUserMapper.update(any(), any())).thenReturn(2);

            boolean result = sysUserService.deleteUserByIds(userIds);

            assertTrue(result);
            verify(sysUserMapper, times(1)).update(any(), any());
        }
    }

    @Test
    @DisplayName("测试重置用户密码 - 成功场景")
    void testResetUserPassword_Success() {
        try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class);
             MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {

            secUtils.when(() -> SecurityUtils.encryptPassword(anyString())).thenReturn("$2a$10$newEncodedPassword");
            secCtx.when(SecurityContextUtils::getUsername).thenReturn("admin");

            when(sysUserMapper.selectById(1L)).thenReturn(testUser);
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);
            doNothing().when(userPermissionCacheService).clearUserPermissionCache(anyString());
            doNothing().when(tokenService).forceLogout(anyLong(), anyString());

            boolean result = sysUserService.resetUserPassword(1L, "newPassword123");

            assertTrue(result);
            secUtils.verify(() -> SecurityUtils.encryptPassword("newPassword123"));
            verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
        }
    }

    @Test
    @DisplayName("测试修改用户状态 - 成功场景")
    void testChangeUserStatus_Success() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUsername).thenReturn("admin");

            when(sysUserMapper.selectById(1L)).thenReturn(testUser);
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            boolean result = sysUserService.changeUserStatus(1L, "1");

            assertTrue(result);
            verify(sysUserMapper, times(1)).updateById(any(SysUser.class));
        }
    }

    @Test
    @DisplayName("测试检查是否允许删除用户 - 不能删除自己")
    void testCanDeleteUsers_CannotDeleteSelf() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUserId).thenReturn(1L);

            List<Long> userIds = Arrays.asList(1L);
            String reason = sysUserService.canDeleteUsers(userIds);

            assertNotNull(reason);
            assertTrue(reason.contains("不能删除当前登录用户"));
        }
    }

    @Test
    @DisplayName("测试检查是否允许重置密码 - 不能重置自己密码")
    void testCanResetPassword_CannotResetSelf() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUserId).thenReturn(1L);

            Long targetUserId = 1L;
            String reason = sysUserService.canResetPassword(targetUserId);

            assertNotNull(reason);
            assertTrue(reason.contains("不能重置当前登录用户密码"));
        }
    }

    @Test
    @DisplayName("测试判断当前用户是否是超级管理员")
    void testIsCurrentUserSuperAdmin() {
        try (MockedStatic<SecurityContextUtils> secCtx = mockStatic(SecurityContextUtils.class)) {
            secCtx.when(SecurityContextUtils::getUserId).thenReturn(null);
            assertFalse(sysUserService.isCurrentUserSuperAdmin());
        }
    }
}
