package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMonitorSlowSql;
import org.apache.ibatis.annotations.Mapper;

/**
 * 慢SQL记录 Mapper 接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysMonitorSlowSqlMapper extends BaseMapper<SysMonitorSlowSql> {
}
