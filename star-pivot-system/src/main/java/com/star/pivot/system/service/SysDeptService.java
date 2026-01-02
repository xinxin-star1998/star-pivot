package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.system.domain.bo.DeptVO;
import com.star.pivot.system.domain.dto.DeptDTO;
import com.star.pivot.system.domain.entity.SysDept;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author stardust
 * @date 2024-01-01
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树列表
     *
     * @return 部门树列表
     */
    List<DeptVO> selectDeptTree();

    /**
     * 根据部门ID查询部门详情
     *
     * @param deptId 部门ID
     * @return 部门详情
     */
    DeptVO selectDeptById(Long deptId);

    /**
     * 新增部门
     *
     * @param deptDTO 部门信息
     * @return 是否成功
     */
    boolean insertDept(DeptDTO deptDTO);

    /**
     * 修改部门
     *
     * @param deptDTO 部门信息
     * @return 是否成功
     */
    boolean updateDept(DeptDTO deptDTO);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 是否成功
     */
    boolean deleteDept(Long deptId);

    /**
     * 检查部门名称是否唯一
     *
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @param deptId 部门ID（修改时传入）
     * @return 是否唯一
     */
    boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId);

    /**
     * 检查部门是否有子部门
     *
     * @param deptId 部门ID
     * @return 是否有子部门
     */
    boolean hasChildDept(Long deptId);

    /**
     * 检查部门是否有用户
     *
     * @param deptId 部门ID
     * @return 是否有用户
     */
    boolean hasUser(Long deptId);
}

