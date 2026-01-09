package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息表(SysUser)表数据库访问层
 *
 * @author xinxin
 * @since 2025-12-28 17:28:22
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> selectPageList(Page<SysUser> page, @Param("userReqBo") UserReqBo userReqBo);

    List<SysRole> getRolesByUserId(@Param("userId") Long userId);

    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);
}

