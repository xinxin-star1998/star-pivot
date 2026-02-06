package com.star.pivot.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.quartz.domain.bo.SysJobLogVO;
import com.star.pivot.quartz.domain.bo.SysJobVO;
import com.star.pivot.quartz.domain.dto.SysJobDTO;
import com.star.pivot.quartz.domain.dto.SysJobLogQueryDTO;
import com.star.pivot.quartz.domain.dto.SysJobQueryDTO;
import com.star.pivot.quartz.domain.entity.SysJob;

import java.util.List;

/**
 * 定时任务 Service 接口
 *
 * @author StarPivot
 */
public interface ISysJobService extends IService<SysJob> {

    PageResponse<SysJobVO> selectJobPage(SysJobQueryDTO query);

    SysJobVO getJobById(Long jobId);

    boolean insertJob(SysJobDTO dto);

    boolean updateJob(SysJobDTO dto);

    boolean deleteJobByIds(List<Long> jobIds);

    boolean changeStatus(Long jobId, String status);

    boolean runOnce(Long jobId);

    void executeJob(Long jobId);

    PageResponse<SysJobLogVO> selectJobLogPage(SysJobLogQueryDTO query);

    boolean clearJobLog();

    void loadScheduledJobsOnStartup();
}
