package com.star.pivot.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.quartz.domain.dto.SysJobQueryDTO;
import com.star.pivot.quartz.domain.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 定时任务 Mapper
 *
 * @author StarPivot
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

    IPage<SysJob> selectJobPage(Page<SysJob> page, @Param("query") SysJobQueryDTO query);
}
