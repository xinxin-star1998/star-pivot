package com.star.pivot.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.workflow.domain.entity.WfTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WfTaskMapper extends BaseMapper<WfTask> {

    @Select("<script>" +
            "SELECT t.* FROM wf_task t " +
            "LEFT JOIN wf_instance i ON t.instance_id = i.instance_id " +
            "${ew.customSqlSegment}" +
            "</script>")
    IPage<WfTask> selectPageWithInstance(Page<WfTask> page, @Param(Constants.WRAPPER) Wrapper<WfTask> wrapper);
}
