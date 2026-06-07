package com.star.pivot.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.workflow.domain.entity.WfInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WfInstanceMapper extends BaseMapper<WfInstance> {
}
