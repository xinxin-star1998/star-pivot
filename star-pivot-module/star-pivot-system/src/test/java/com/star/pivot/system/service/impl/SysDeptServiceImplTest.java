package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.mapper.SysUserMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("部门服务测试")
class SysDeptServiceImplTest {

    @Mock
    private SysDeptMapper sysDeptMapper;

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysDeptServiceImpl sysDeptService;

    private SysDept testDept;
    private DeptDTO testDeptDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sysDeptService, "baseMapper", sysDeptMapper);

        testDept = new SysDept();
        testDept.setDeptId(1L);
        testDept.setDeptName("测试部门");
        testDept.setParentId(0L);
        testDept.setAncestors("0");
        testDept.setOrderNum(1);
        testDept.setLeader("张三");
        testDept.setPhone("13800138000");
        testDept.setEmail("dept@example.com");
        testDept.setStatus("0");
        testDept.setDelFlag("0");
        testDept.setCreateTime(LocalDateTime.now());

        testDeptDTO = new DeptDTO();
        testDeptDTO.setDeptName("新部门");
        testDeptDTO.setParentId(0L);
        testDeptDTO.setOrderNum(1);
        testDeptDTO.setLeader("李四");
        testDeptDTO.setPhone("13900139000");
        testDeptDTO.setEmail("newdept@example.com");
    }

    @Nested
    @DisplayName("部门查询测试")
    class QueryDeptTests {

        @Test
        @DisplayName("查询部门树 - 成功")
        void selectDeptTree_Success() {
            SysDept dept1 = new SysDept();
            dept1.setDeptId(1L);
            dept1.setDeptName("总公司");
            dept1.setParentId(0L);
            dept1.setOrderNum(1);
            dept1.setStatus("0");
            dept1.setDelFlag("0");

            SysDept dept2 = new SysDept();
            dept2.setDeptId(2L);
            dept2.setDeptName("技术部");
            dept2.setParentId(1L);
            dept2.setOrderNum(1);
            dept2.setStatus("0");
            dept2.setDelFlag("0");

            when(sysDeptMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(dept1, dept2));

            List<DeptVO> result = sysDeptService.selectDeptTree();

            assertNotNull(result);
            verify(sysDeptMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("查询部门树 - 空列表")
        void selectDeptTree_Empty() {
            when(sysDeptMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            List<DeptVO> result = sysDeptService.selectDeptTree();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("根据ID查询部门 - 部门存在")
        void selectDeptById_DeptExists() {
            when(sysDeptMapper.selectById(1L)).thenReturn(testDept);

            DeptVO result = sysDeptService.selectDeptById(1L);

            assertNotNull(result);
            assertEquals("测试部门", result.getDeptName());
        }

        @Test
        @DisplayName("根据ID查询部门 - 部门不存在")
        void selectDeptById_DeptNotExists() {
            when(sysDeptMapper.selectById(999L)).thenReturn(null);

            assertThrows(BusinessException.class, () -> sysDeptService.selectDeptById(999L));
        }
    }

    @Nested
    @DisplayName("部门新增测试")
    class AddDeptTests {

        @Test
        @DisplayName("新增部门 - 成功")
        void insertDept_Success() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysDeptMapper.insert(any(SysDept.class))).thenReturn(1);

            assertDoesNotThrow(() -> sysDeptService.insertDept(testDeptDTO));
        }

        @Test
        @DisplayName("新增部门 - 部门名称已存在")
        void insertDept_NameExists() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            assertThrows(BusinessException.class, () -> sysDeptService.insertDept(testDeptDTO));
        }
    }

    @Nested
    @DisplayName("部门更新测试")
    class UpdateDeptTests {

        @Test
        @DisplayName("更新部门 - 成功")
        void updateDept_Success() {
            testDeptDTO.setDeptId(1L);
            
            when(sysDeptMapper.selectById(1L)).thenReturn(testDept);
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysDeptMapper.updateById(any(SysDept.class))).thenReturn(1);

            assertDoesNotThrow(() -> sysDeptService.updateDept(testDeptDTO));
        }

        @Test
        @DisplayName("更新部门 - 部门不存在")
        void updateDept_DeptNotExists() {
            testDeptDTO.setDeptId(999L);
            
            when(sysDeptMapper.selectById(999L)).thenReturn(null);

            assertThrows(BusinessException.class, () -> sysDeptService.updateDept(testDeptDTO));
        }

        @Test
        @DisplayName("更新部门 - 不能将父部门设置为自己")
        void updateDept_CannotSetParentToSelf() {
            testDeptDTO.setDeptId(1L);
            testDeptDTO.setParentId(1L);
            
            when(sysDeptMapper.selectById(1L)).thenReturn(testDept);

            assertThrows(BusinessException.class, () -> sysDeptService.updateDept(testDeptDTO));
        }
    }

    @Nested
    @DisplayName("部门删除测试")
    class DeleteDeptTests {

        @Test
        @DisplayName("删除部门 - 成功")
        void deleteDeptByIds_Success() {
            when(sysDeptMapper.selectById(1L)).thenReturn(testDept);
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(sysDeptMapper.updateById(any(SysDept.class))).thenReturn(1);

            List<Long> deptIds = Arrays.asList(1L);

            assertDoesNotThrow(() -> sysDeptService.deleteDeptByIds(deptIds));
        }

        @Test
        @DisplayName("删除部门 - 存在子部门")
        void deleteDeptByIds_HasChildren() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

            List<Long> deptIds = Arrays.asList(1L);

            assertThrows(BusinessException.class, () -> sysDeptService.deleteDeptByIds(deptIds));
        }

        @Test
        @DisplayName("删除部门 - 存在用户")
        void deleteDeptByIds_HasUsers() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

            List<Long> deptIds = Arrays.asList(1L);

            assertThrows(BusinessException.class, () -> sysDeptService.deleteDeptByIds(deptIds));
        }
    }

    @Nested
    @DisplayName("部门校验测试")
    class ValidateDeptTests {

        @Test
        @DisplayName("检查部门名称唯一性 - 唯一")
        void checkDeptNameUnique_Unique() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            boolean result = sysDeptService.checkDeptNameUnique("新部门", 0L, null);

            assertTrue(result);
        }

        @Test
        @DisplayName("检查部门名称唯一性 - 不唯一")
        void checkDeptNameUnique_NotUnique() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            boolean result = sysDeptService.checkDeptNameUnique("测试部门", 0L, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("检查是否有子部门 - 有子部门")
        void hasChildDept_HasChildren() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

            boolean result = sysDeptService.hasChildDept(1L);

            assertTrue(result);
        }

        @Test
        @DisplayName("检查是否有子部门 - 无子部门")
        void hasChildDept_NoChildren() {
            when(sysDeptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            boolean result = sysDeptService.hasChildDept(1L);

            assertFalse(result);
        }

        @Test
        @DisplayName("检查是否有用户 - 有用户")
        void hasUser_HasUsers() {
            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

            boolean result = sysDeptService.hasUser(1L);

            assertTrue(result);
        }

        @Test
        @DisplayName("检查是否有用户 - 无用户")
        void hasUser_NoUsers() {
            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            boolean result = sysDeptService.hasUser(1L);

            assertFalse(result);
        }
    }
}
