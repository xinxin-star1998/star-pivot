package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
