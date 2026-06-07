package com.star.pivot.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.workflow.domain.entity.WfProcessDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WfProcessDefMapper extends BaseMapper<WfProcessDef> {

    @Select("SELECT COALESCE(MAX(version), 0) FROM wf_process_def WHERE process_code = #{processCode}")
    Integer selectMaxVersion(@Param("processCode") String processCode);
}
