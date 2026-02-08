package com.star.pivot.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.constants.Constants;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.quartz.domain.bo.SysJobLogVO;
import com.star.pivot.quartz.domain.bo.SysJobVO;
import com.star.pivot.quartz.domain.dto.SysJobDTO;
import com.star.pivot.quartz.domain.dto.SysJobLogQueryDTO;
import com.star.pivot.quartz.domain.dto.SysJobQueryDTO;
import com.star.pivot.quartz.domain.entity.SysJob;
import com.star.pivot.quartz.domain.entity.SysJobLog;
import com.star.pivot.quartz.execution.QuartzJobExecution;
import com.star.pivot.quartz.mapper.SysJobLogMapper;
import com.star.pivot.quartz.mapper.SysJobMapper;
import com.star.pivot.quartz.service.ISysJobService;
import com.star.pivot.security.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {

    private final SysJobLogMapper jobLogMapper;
    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    private static final String JOB_KEY_PREFIX = "JOB_";

    @Override
    public PageResponse<SysJobVO> selectJobPage(SysJobQueryDTO query) {
        Page<SysJob> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysJob> result = baseMapper.selectJobPage(page, query);
        List<SysJobVO> list = result.getRecords().stream().map(this::toVO).toList();
        PageResponse<SysJobVO> resp = new PageResponse<>();
        resp.setTotal(result.getTotal());
        resp.setRows(list);
        resp.setPageNum(result.getCurrent());
        resp.setPageSize(result.getSize());
        resp.setPageCount(result.getPages());
        return resp;
    }

    @Override
    public SysJobVO getJobById(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) throw new BusinessException("任务不存在");
        return toVO(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertJob(SysJobDTO dto) {
        validateInvokeTarget(dto.getInvokeTarget());
        SysJob job = toEntity(dto);
        job.setCreateTime(LocalDateTime.now());
        job.setUpdateTime(LocalDateTime.now());
        job.setCreateBy(SecurityContextUtils.getUsername());
        job.setUpdateBy(SecurityContextUtils.getUsername());
        save(job);
        if (Constants.SUCCESS.equals(job.getStatus())) {
            scheduleJob(job);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(SysJobDTO dto) {
        if (dto.getJobId() == null) throw new BusinessException("任务ID不能为空");
        validateInvokeTarget(dto.getInvokeTarget());
        SysJob exist = getById(dto.getJobId());
        if (exist == null) throw new BusinessException("任务不存在");
        unscheduleJob(exist);
        SysJob job = toEntity(dto);
        job.setJobId(dto.getJobId());
        job.setUpdateTime(LocalDateTime.now());
        job.setUpdateBy(SecurityContextUtils.getUsername());
        updateById(job);
        if (Constants.SUCCESS.equals(job.getStatus())) {
            scheduleJob(job);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobByIds(List<Long> jobIds) {
        for (Long id : jobIds) {
            SysJob job = getById(id);
            if (job != null) unscheduleJob(job);
        }
        removeByIds(jobIds);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Long jobId, String status) {
        SysJob job = getById(jobId);
        if (job == null) throw new BusinessException("任务不存在");
        unscheduleJob(job);
        job.setStatus(status);
        job.setUpdateTime(LocalDateTime.now());
        job.setUpdateBy(SecurityContextUtils.getUsername());
        updateById(job);
        if (Constants.SUCCESS.equals(status)) scheduleJob(job);
        return true;
    }

    @Override
    public boolean runOnce(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) throw new BusinessException("任务不存在");
        try {
            JobKey jobKey = JobKey.jobKey(JOB_KEY_PREFIX + jobId);
            if (!scheduler.checkExists(jobKey)) scheduleJob(job);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            log.error("立即执行任务失败 jobId={}", jobId, e);
            throw new BusinessException("立即执行失败：" + e.getMessage());
        }
        return true;
    }

    @Override
    public void executeJob(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            log.warn("定时任务不存在 jobId={}", jobId);
            return;
        }
        SysJobLog logEntity = new SysJobLog();
        logEntity.setJobName(job.getJobName());
        logEntity.setJobGroup(job.getJobGroup());
        logEntity.setInvokeTarget(job.getInvokeTarget());
        logEntity.setCreateTime(LocalDateTime.now());
        try {
            invokeMethod(job.getInvokeTarget());
            logEntity.setStatus(Constants.SUCCESS);
            logEntity.setJobMessage("执行成功");
        } catch (Exception e) {
            log.error("定时任务执行异常 jobId={}, target={}", jobId, job.getInvokeTarget(), e);
            logEntity.setStatus(Constants.FAIL);
            logEntity.setJobMessage("执行失败");
            logEntity.setExceptionInfo(e.getMessage() != null ? e.getMessage().substring(0, Math.min(2000, e.getMessage().length())) : "");
        }
        jobLogMapper.insert(logEntity);
    }

    @Override
    public PageResponse<SysJobLogVO> selectJobLogPage(SysJobLogQueryDTO query) {
        Page<SysJobLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysJobLog> result = jobLogMapper.selectJobLogPage(page, query);
        List<SysJobLogVO> list = result.getRecords().stream().map(this::logToVO).toList();
        PageResponse<SysJobLogVO> resp = new PageResponse<>();
        resp.setTotal(result.getTotal());
        resp.setRows(list);
        resp.setPageNum(result.getCurrent());
        resp.setPageSize(result.getSize());
        resp.setPageCount(result.getPages());
        return resp;
    }

    @Override
    public boolean clearJobLog() {
        jobLogMapper.delete(null);
        return true;
    }

    @Override
    public void loadScheduledJobsOnStartup() {
        List<SysJob> list = lambdaQuery().eq(SysJob::getStatus, Constants.SUCCESS).list();
        for (SysJob job : list) {
            try {
                scheduleJob(job);
                log.info("加载定时任务: {} ({})", job.getJobName(), job.getJobId());
            } catch (Exception e) {
                log.error("加载定时任务失败 jobId={}", job.getJobId(), e);
            }
        }
    }

    private void scheduleJob(SysJob job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobExecution.class)
                    .withIdentity(JOB_KEY_PREFIX + job.getJobId())
                    .usingJobData("jobId", job.getJobId())
                    .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("TRIGGER_" + job.getJobId())
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()).withMisfireHandlingInstructionDoNothing())
                    .build();
            if (scheduler.checkExists(jobDetail.getKey())) scheduler.deleteJob(jobDetail.getKey());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("调度任务失败 jobId={}", job.getJobId(), e);
            throw new BusinessException("调度失败：" + e.getMessage());
        }
    }

    private void unscheduleJob(SysJob job) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_KEY_PREFIX + job.getJobId());
            if (scheduler.checkExists(jobKey)) scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("移除调度失败 jobId={}", job.getJobId(), e);
        }
    }

    private void validateInvokeTarget(String invokeTarget) {
        if (invokeTarget == null || invokeTarget.isBlank()) throw new BusinessException("调用目标不能为空");
        for (String s : Constants.JOB_ERROR_STR) {
            if (invokeTarget.contains(s)) throw new BusinessException("调用目标包含违规字符");
        }
        boolean inWhitelist = false;
        for (String prefix : Constants.JOB_WHITELIST_STR) {
            if (invokeTarget.startsWith(prefix)) {
                inWhitelist = true;
                break;
            }
        }
        if (!inWhitelist) throw new BusinessException("调用目标不在白名单内，仅允许: " + String.join(", ", Constants.JOB_WHITELIST_STR));
        if (!invokeTarget.matches("^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*\\.([a-zA-Z0-9_]+)\\(\\)$")) {
            throw new BusinessException("调用目标格式错误，应为: 包名.类名.方法名()");
        }
    }

    private void invokeMethod(String invokeTarget) throws Exception {
        int lastDot = invokeTarget.lastIndexOf('.');
        if (lastDot <= 0 || !invokeTarget.endsWith("()")) throw new IllegalArgumentException("调用目标格式错误");
        String className = invokeTarget.substring(0, lastDot);
        String methodPart = invokeTarget.substring(lastDot + 1);
        String methodName = methodPart.substring(0, methodPart.length() - 2);
        Class<?> clazz = Class.forName(className);
        Object bean = applicationContext.getBean(clazz);
        bean.getClass().getMethod(methodName).invoke(bean);
    }

    private SysJob toEntity(SysJobDTO dto) {
        SysJob job = new SysJob();
        job.setJobName(dto.getJobName());
        job.setJobGroup(dto.getJobGroup() != null ? dto.getJobGroup() : "DEFAULT");
        job.setInvokeTarget(dto.getInvokeTarget());
        job.setCronExpression(dto.getCronExpression());
        job.setMisfirePolicy(dto.getMisfirePolicy() != null ? dto.getMisfirePolicy() : "3");
        job.setConcurrent(dto.getConcurrent() != null ? dto.getConcurrent() : "1");
        job.setStatus(dto.getStatus() != null ? dto.getStatus() : "0");
        job.setRemark(dto.getRemark());
        return job;
    }

    private SysJobVO toVO(SysJob e) {
        SysJobVO vo = new SysJobVO();
        vo.setJobId(e.getJobId());
        vo.setJobName(e.getJobName());
        vo.setJobGroup(e.getJobGroup());
        vo.setInvokeTarget(e.getInvokeTarget());
        vo.setCronExpression(e.getCronExpression());
        vo.setMisfirePolicy(e.getMisfirePolicy());
        vo.setConcurrent(e.getConcurrent());
        vo.setStatus(e.getStatus());
        vo.setCreateBy(e.getCreateBy());
        vo.setCreateTime(e.getCreateTime());
        vo.setUpdateBy(e.getUpdateBy());
        vo.setUpdateTime(e.getUpdateTime());
        vo.setRemark(e.getRemark());
        return vo;
    }

    private SysJobLogVO logToVO(SysJobLog e) {
        SysJobLogVO vo = new SysJobLogVO();
        vo.setJobLogId(e.getJobLogId());
        vo.setJobName(e.getJobName());
        vo.setJobGroup(e.getJobGroup());
        vo.setInvokeTarget(e.getInvokeTarget());
        vo.setJobMessage(e.getJobMessage());
        vo.setStatus(e.getStatus());
        vo.setExceptionInfo(e.getExceptionInfo());
        vo.setCreateTime(e.getCreateTime());
        return vo;
    }
}
