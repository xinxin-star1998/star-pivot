package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联Mapper接口
 *
 * @author stardust
 * @date 2024-01-01
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}

