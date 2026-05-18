package com.star.pivot.system.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.AssignUserReqBo;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.framework.excel.ExcelImportResult;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.domain.excel.SysUserExcel;

import java.util.List;

/**
 * 用户信息表(SysUser)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 17:28:24
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    PageResponse<UserVO> pageList(UserReqBo userReqBo);

    SysUser getUserByUsername(String username);

    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 使用关联查询优化，一次性获取用户及其角色信息
     * 替代多次查询，提升性能
     * 
     * @param userId 用户ID
     * @return 用户信息（包含角色列表）
     */
    SysUser getUserWithRoles(Long userId);

    List<SysMenu> getMenuByUserId(Long userId);

    boolean addUser(UserDTO userDTO);

    UserVO selectByUserId(Long userId);

    boolean updateUser(UserDTO userDTO);

    boolean changeUserStatus(Long userId, String status);

    boolean resetUserPassword(Long userId, String password);

    /**
     * 当前登录用户修改密码（需校验旧密码）
     *
     * @param userId      当前登录用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updateUserPassword(Long userId, String oldPassword, String newPassword);

    /**
     * 删除用户（支持单删和批量删除）
     *
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean deleteUserByIds(List<Long> userIds);

    PageResponse<SysUser> getUserListByRoleId(AssignUserReqBo assignUserReqBo);

    PageResponse<SysUser> unallocatedList(AssignUserReqBo assignUserReqBo);

    /** EasyExcel 导出用户列表 */
    List<SysUserExcel> listForExport(UserReqBo userReqBo);

    /** EasyExcel 导入用户 */
    ExcelImportResult importFromExcel(List<SysUserExcel> rows, boolean updateSupport);
}

