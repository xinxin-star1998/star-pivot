package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.system.domain.dto.RoleDTO;
import com.star.pivot.system.domain.dto.RoleQueryDTO;
import com.star.pivot.system.domain.entity.RoleMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.*;
import com.star.pivot.system.service.UserPermissionCacheService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务测试")
class SysRoleServiceImplTest {

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @Mock
    private RoleDeptMapper roleDeptMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private SysDeptMapper sysDeptMapper;

    @Mock
    private SysMenuMapper sysMenuMapper;

    @Mock
    private UserPermissionCacheService userPermissionCacheService;

    @InjectMocks
    private SysRoleServiceImpl sysRoleService;

    private SysRole testRole;
    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sysRoleService, "baseMapper", sysRoleMapper);

        testRole = new SysRole();
        testRole.setRoleId(1L);
        testRole.setRoleName("测试角色");
        testRole.setRoleKey("test_role");
        testRole.setRoleSort(1);
        testRole.setStatus("0");
        testRole.setDelFlag("0");
        testRole.setCreateTime(LocalDateTime.now());

        testRoleDTO = new RoleDTO();
        testRoleDTO.setRoleName("新角色");
        testRoleDTO.setRoleKey("new_role");
        testRoleDTO.setRoleSort(2);
        testRoleDTO.setMenuIds(Arrays.asList(1L, 2L, 3L));
    }

    @Nested
    @DisplayName("角色查询测试")
    class QueryRoleTests {

        @Test
        @DisplayName("根据ID查询角色 - 角色存在")
        void selectRoleById_RoleExists() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);

            SysRole result = sysRoleService.selectRoleById(1L);

            assertNotNull(result);
            assertEquals("测试角色", result.getRoleName());
            verify(sysRoleMapper, times(1)).selectById(1L);
        }

        @Test
        @DisplayName("根据ID查询角色 - 角色不存在")
        void selectRoleById_RoleNotExists() {
            when(sysRoleMapper.selectById(999L)).thenReturn(null);

            SysRole result = sysRoleService.selectRoleById(999L);

            assertNull(result);
        }

        @Test
        @DisplayName("分页查询角色列表")
        void selectRoleList() {
            RoleQueryDTO queryDTO = new RoleQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10);

            Page<SysRole> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testRole));
            page.setTotal(1);

            when(sysRoleMapper.selectPageList(any(Page.class), any())).thenReturn(page);

            PageResponse<SysRole> result = sysRoleService.selectRoleList(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRows().size());
        }
    }

    @Nested
    @DisplayName("角色新增测试")
    class AddRoleTests {

        @Test
        @DisplayName("新增角色 - 成功")
        void insertRole_Success() {
            when(sysRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysRoleMapper.insert(any(SysRole.class))).thenReturn(1);
            when(roleMenuMapper.insert(any(RoleMenu.class))).thenReturn(1);

            assertDoesNotThrow(() -> sysRoleService.insertRole(testRoleDTO));
        }

        @Test
        @DisplayName("新增角色 - 角色权限字符串已存在")
        void insertRole_RoleKeyExists() {
            when(sysRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            assertThrows(BusinessException.class, () -> sysRoleService.insertRole(testRoleDTO));
        }
    }

    @Nested
    @DisplayName("角色更新测试")
    class UpdateRoleTests {

        @Test
        @DisplayName("更新角色 - 成功")
        void updateRole_Success() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);
            when(sysRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysRoleMapper.updateById(any(SysRole.class))).thenReturn(1);

            testRoleDTO.setRoleId(1L);

            assertDoesNotThrow(() -> sysRoleService.updateRole(testRoleDTO));
        }

        @Test
        @DisplayName("更新角色 - 角色不存在")
        void updateRole_RoleNotExists() {
            when(sysRoleMapper.selectById(999L)).thenReturn(null);

            testRoleDTO.setRoleId(999L);

            assertThrows(BusinessException.class, () -> sysRoleService.updateRole(testRoleDTO));
        }
    }

    @Nested
    @DisplayName("角色删除测试")
    class DeleteRoleTests {

        @Test
        @DisplayName("删除角色 - 成功")
        void deleteRoleByIds_Success() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);
            when(userRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysRoleMapper.updateById(any(SysRole.class))).thenReturn(1);

            List<Long> roleIds = Arrays.asList(1L);

            assertDoesNotThrow(() -> sysRoleService.deleteRoleByIds(roleIds));
        }

        @Test
        @DisplayName("删除角色 - 角色已被使用")
        void deleteRoleByIds_RoleInUse() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);
            when(userRoleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

            List<Long> roleIds = Arrays.asList(1L);

            assertThrows(BusinessException.class, () -> sysRoleService.deleteRoleByIds(roleIds));
        }

        @Test
        @DisplayName("删除角色 - 不能删除超级管理员")
        void deleteRoleByIds_CannotDeleteAdmin() {
            SysRole adminRole = new SysRole();
            adminRole.setRoleId(1L);
            adminRole.setRoleKey(AppConstants.ADMIN_ROLE_KEY);
            adminRole.setRoleName("超级管理员");
            adminRole.setDelFlag("0");

            when(sysRoleMapper.selectById(1L)).thenReturn(adminRole);

            List<Long> roleIds = Arrays.asList(1L);

            assertThrows(BusinessException.class, () -> sysRoleService.deleteRoleByIds(roleIds));
        }
    }

    @Nested
    @DisplayName("角色状态测试")
    class RoleStatusTests {

        @Test
        @DisplayName("修改角色状态 - 成功")
        void changeRoleStatus_Success() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);
            when(sysRoleMapper.updateById(any(SysRole.class))).thenReturn(1);

            boolean result = sysRoleService.changeRoleStatus(1L, "1");

            assertTrue(result);
        }

        @Test
        @DisplayName("修改角色状态 - 不能停用超级管理员")
        void changeRoleStatus_CannotDisableAdmin() {
            SysRole adminRole = new SysRole();
            adminRole.setRoleId(1L);
            adminRole.setRoleKey(AppConstants.ADMIN_ROLE_KEY);
            adminRole.setDelFlag("0");

            when(sysRoleMapper.selectById(1L)).thenReturn(adminRole);

            assertThrows(BusinessException.class, () -> sysRoleService.changeRoleStatus(1L, "1"));
        }
    }

    @Nested
    @DisplayName("角色权限测试")
    class RolePermissionTests {

        @Test
        @DisplayName("获取角色菜单ID列表")
        void getMenuIdsByRoleId() {
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMenuMapper.selectMenuIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

            List<Long> menuIds = sysRoleService.getMenuIdsByRoleId(1L);

            assertNotNull(menuIds);
            assertEquals(3, menuIds.size());
        }

        @Test
        @DisplayName("获取角色菜单ID列表 - 角色已禁用")
        void getMenuIdsByRoleId_RoleDisabled() {
            testRole.setStatus("1");
            when(sysRoleMapper.selectById(1L)).thenReturn(testRole);

            assertThrows(BusinessException.class, () -> sysRoleService.getMenuIdsByRoleId(1L));
        }
    }
}
