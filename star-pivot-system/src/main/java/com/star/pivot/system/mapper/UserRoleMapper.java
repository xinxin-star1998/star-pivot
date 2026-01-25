package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author stardust
 * @since 2024-01-01
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 批量插入用户角色关联关系
     *
     * @param list 用户角色关联集合
     * @return 影响行数
     */
    int insertBatchUserRoles(@Param("list") List<UserRole> list);

    boolean deleteByRoleIdAndUserId(Long roleId, Long userId);
}

