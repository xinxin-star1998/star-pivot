package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.system.service.SysDeptService;
import com.star.pivot.system.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务实现类
 *
 * @author stardust
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysUserMapper userMapper;

    @Override
    public List<DeptVO> selectDeptTree() {
        List<SysDept> depts = this.list(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDelFlag, "0"));
        return buildDeptTree(depts, 0L);
    }

    @Override
    public DeptVO selectDeptById(Long deptId) {
        SysDept dept = this.getById(deptId);
        if (dept == null || "2".equals(dept.getDelFlag())) {
            throw new BusinessException("部门不存在");
        }
        DeptVO vo = new DeptVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDept(DeptDTO deptDTO) {
        // 检查部门名称是否唯一
        if (!checkDeptNameUnique(deptDTO.getDeptName(), deptDTO.getParentId(), null)) {
            throw new BusinessException("部门名称已存在");
        }

        // 创建部门
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(deptDTO, dept);
        dept.setParentId(deptDTO.getParentId() != null ? deptDTO.getParentId() : 0L);
        dept.setOrderNum(deptDTO.getOrderNum() != null ? deptDTO.getOrderNum() : 0);
        dept.setStatus(StringUtils.hasText(deptDTO.getStatus()) ? deptDTO.getStatus() : "0");
        dept.setDelFlag("0");

        // 设置祖级列表
        if (dept.getParentId() != null && dept.getParentId() != 0L) {
            SysDept parentDept = this.getById(dept.getParentId());
            if (parentDept != null) {
                String ancestors = parentDept.getAncestors();
                dept.setAncestors(ancestors + "," + dept.getParentId());
            } else {
                dept.setAncestors("0," + dept.getParentId());
            }
        } else {
            dept.setAncestors("0");
        }

        String currentUser = SecurityContextUtils.getUsername();
        dept.setCreateBy(currentUser);
        dept.setCreateTime(LocalDateTime.now());

        return this.save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(DeptDTO deptDTO) {
        SysDept dept = this.getById(deptDTO.getDeptId());
        if (dept == null || "2".equals(dept.getDelFlag())) {
            throw new BusinessException("部门不存在");
        }

        // 不能将父部门设置为自己的子部门
        if (deptDTO.getParentId() != null && deptDTO.getParentId().equals(deptDTO.getDeptId())) {
            throw new BusinessException("不能将父部门设置为自己的子部门");
        }

        // 检查部门名称是否唯一
        if (!checkDeptNameUnique(deptDTO.getDeptName(), deptDTO.getParentId(), deptDTO.getDeptId())) {
            throw new BusinessException("部门名称已存在");
        }

        // 如果修改了父部门，需要更新祖级列表
        if (deptDTO.getParentId() != null && !deptDTO.getParentId().equals(dept.getParentId())) {
            SysDept newParentDept = this.getById(deptDTO.getParentId());
            if (newParentDept != null) {
                String ancestors = newParentDept.getAncestors();
                deptDTO.setAncestors(ancestors + "," + deptDTO.getParentId());
            } else {
                deptDTO.setAncestors("0," + deptDTO.getParentId());
            }
            
            // 更新子部门的祖级列表
            updateChildrenAncestors(deptDTO.getDeptId(), deptDTO.getAncestors());
        }

        // 更新部门信息
        BeanUtils.copyProperties(deptDTO, dept, "deptId");
        String currentUser = SecurityContextUtils.getUsername();
        dept.setUpdateBy(currentUser);
        dept.setUpdateTime(LocalDateTime.now());

        return this.updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long deptId) {
        // 检查是否有子部门
        if (hasChildDept(deptId)) {
            throw new BusinessException("存在子部门，不允许删除");
        }

        // 检查是否有用户
        if (hasUser(deptId)) {
            throw new BusinessException("部门存在用户，不允许删除");
        }

        // 软删除
        SysDept dept = this.getById(deptId);
        dept.setDelFlag("2");
        String currentUser = SecurityContextUtils.getUsername();
        dept.setUpdateBy(currentUser);
        dept.setUpdateTime(LocalDateTime.now());

        return this.updateById(dept);
    }

    @Override
    public boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptName, deptName)
                .eq(SysDept::getParentId, parentId != null ? parentId : 0L)
                .eq(SysDept::getDelFlag, "0");
        if (deptId != null) {
            wrapper.ne(SysDept::getDeptId, deptId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public boolean hasChildDept(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId)
                .eq(SysDept::getDelFlag, "0");
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean hasUser(Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeptId, deptId)
                .eq(SysUser::getDelFlag, "0");
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建部门树
     */
    private List<DeptVO> buildDeptTree(List<SysDept> depts, Long parentId) {
        List<DeptVO> deptTree = new ArrayList<>();
        
        for (SysDept dept : depts) {
            if (parentId.equals(dept.getParentId())) {
                DeptVO deptVO = new DeptVO();
                BeanUtils.copyProperties(dept, deptVO);
                
                // 递归构建子部门
                List<DeptVO> children = buildDeptTree(depts, dept.getDeptId());
                deptVO.setChildren(children.isEmpty() ? null : children);
                
                deptTree.add(deptVO);
            }
        }
        
        // 按显示顺序排序
        deptTree.sort((a, b) -> {
            int orderA = a.getOrderNum() != null ? a.getOrderNum() : 0;
            int orderB = b.getOrderNum() != null ? b.getOrderNum() : 0;
            return Integer.compare(orderA, orderB);
        });
        
        return deptTree;
    }

    /**
     * 更新子部门的祖级列表
     */
    private void updateChildrenAncestors(Long deptId, String ancestors) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId)
                .eq(SysDept::getDelFlag, "0");
        List<SysDept> children = this.list(wrapper);
        
        for (SysDept child : children) {
            String newAncestors = ancestors + "," + deptId;
            child.setAncestors(newAncestors);
            this.updateById(child);
            
            // 递归更新子部门的子部门
            updateChildrenAncestors(child.getDeptId(), newAncestors);
        }
    }
}

