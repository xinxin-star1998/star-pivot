package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限表(SysMenu)表数据库访问层
 *
 * @author makejava
 * @since 2025-12-28 19:39:05
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectPermissionsByUserId(@Param("userId") Long userId);

    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);
}

