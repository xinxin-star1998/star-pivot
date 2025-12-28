package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysRole;

import java.util.List;

/**
 * 角色信息表(SysRole)表数据库访问层
 *
 * @author makejava
 * @since 2025-12-28 19:31:33
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolesByUserId(Long userId);
}

