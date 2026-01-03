package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.RoleDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色部门关联Mapper接口
 *
 * @author stardust
 * @date 2024-01-01
 */
@Mapper
public interface RoleDeptMapper extends BaseMapper<RoleDept> {
}

