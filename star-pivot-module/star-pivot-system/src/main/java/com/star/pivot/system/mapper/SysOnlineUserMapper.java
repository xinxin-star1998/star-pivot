package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysOnlineUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在线用户记录 Mapper 接口
 * <p>
 * 说明：用于存储在线用户的历史记录，实时数据存储在 Redis 中。
 * </p>
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysOnlineUserMapper extends BaseMapper<SysOnlineUser> {
}
