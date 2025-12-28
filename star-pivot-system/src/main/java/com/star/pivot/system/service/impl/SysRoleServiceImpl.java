package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.mapper.SysRoleMapper;
import com.star.pivot.system.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色信息表(SysRole)表服务实现类
 *
 * @author makejava
 * @since 2025-12-28 19:32:10
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}

