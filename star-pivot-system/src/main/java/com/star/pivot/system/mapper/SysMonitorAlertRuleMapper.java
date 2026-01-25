package com.star.pivot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.system.domain.entity.SysMonitorAlertRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 监控告警规则 Mapper 接口
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Mapper
public interface SysMonitorAlertRuleMapper extends BaseMapper<SysMonitorAlertRule> {
}
