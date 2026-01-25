package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.AssignUserReqBo;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;

import java.util.List;
import java.util.Map;

/**
 * 用户信息表(SysUser)表服务接口
 *
 * @author makejava
 * @since 2025-12-28 17:28:24
 */
public interface SysUserService extends IService<SysUser>, ImportExportService {
    /**
     * 用户分页查询
     *
     * @param userReqBo 查询参数
     * @return 分页结果
     */
    PageResponse<UserVO> pageList(UserReqBo userReqBo);

    SysUser getUserByUsername(String username);

    List<SysRole> getRolesByUserId(Long userId);

    List<SysMenu> getMenuByUserId(Long userId);

    boolean addUser(UserDTO userDTO);

    UserVO selectByUserId(Long userId);

    boolean updateUser(UserDTO userDTO);

    boolean changeUserStatus(Long userId, String status);

    boolean resetUserPassword(Long userId, String password);

    /**
     * 删除用户（支持单删和批量删除）
     *
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean deleteUserByIds(List<Long> userIds);

    PageResponse<SysUser> getUserListByRoleId(AssignUserReqBo assignUserReqBo);

    PageResponse<SysUser> unallocatedList(AssignUserReqBo assignUserReqBo);

    /**
     * 批量导入用户（Excel 导入）
     *
     * @param rowList Excel 解析后的行数据列表（key 为表头中文名）
     * @return 成功导入的用户数量
     */
    int importUsers(List<Map<String, Object>> rowList);
}

