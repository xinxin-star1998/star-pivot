package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户信息表(SysUser)表数据库访问层
 *
 * @author xinxin
 * @since 2025-12-28 17:28:22
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> selectPageList(Page<SysUser> page, @Param("param") Map<String, Object> param);

    /**
     * 按月份统计新增用户数
     * @param start 开始时间
     * @param end 结束时间
     * @return List<Object[]> 每个元素为 [yearMonth, count]
     */
    List<Map<String, Object>> countByMonthRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SysRole> getRolesByUserId(@Param("userId") Long userId);

    /**
     * 使用 LEFT JOIN 一次性查询用户及其角色信息（关联查询优化）
     * 
     * @param userId 用户ID
     * @return 用户信息（包含角色列表）
     */
    SysUser selectUserWithRoles(@Param("userId") Long userId);

    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);

    IPage<SysUser> getUserListByRoleId(Page<SysUser> page, @Param("param") Map<String, Object> param);

    IPage<SysUser> unallocatedList(Page<SysUser> page, @Param("param") Map<String, Object> param);
    /** 根据用户ID查询所属部门ID */
    Long selectDeptIdByUserId(@Param("userId") Long userId);

}

