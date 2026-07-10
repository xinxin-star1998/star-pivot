package com.star.pivot.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.ai.domain.entity.AiUsageLog;
import com.star.pivot.ai.domain.vo.AiUsageSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiUsageLogMapper extends BaseMapper<AiUsageLog> {

    AiUsageSummaryVo selectSummary(@Param("beginTime") String beginTime, @Param("endTime") String endTime);
}
