package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联Mapper接口
 *
 * @author stardust
 * &#064;date  2024-01-01
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    void batchSave(Long roleId, List<Long> menuIds);

    void deleteByRoleId(@Param("roleId") Long roleId);

    List<Long> selectMenuIdsByRoleId(Long roleId);
}

