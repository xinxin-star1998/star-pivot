package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;

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
    PageResponse<SysUser> pageList(UserReqBo userReqBo);

    SysUser getUserByUsername(String username);

    List<SysRole> getRolesByUserId(Long userId);

    List<SysMenu> getMenuByUserId(Long userId);
}

