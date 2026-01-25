package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMonitorAlertRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 监控告警记录 Mapper 接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysMonitorAlertRecordMapper extends BaseMapper<SysMonitorAlertRecord> {
}
