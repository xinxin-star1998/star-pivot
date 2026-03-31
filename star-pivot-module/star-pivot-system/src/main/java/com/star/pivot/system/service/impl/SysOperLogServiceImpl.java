package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.OperLogReqBo;
import com.star.pivot.system.domain.bo.OperLogVO;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.mapper.SysOperLogMapper;
import com.star.pivot.system.service.interfaces.SysOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现类
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Slf4j
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    /**
     * 保存操作日志
     * 使用异步方式保存，避免影响主流程性能
     *
     * @param operLog 操作日志
     */
    @Override
    @Async
    public void saveOperLog(SysOperLog operLog) {
        try {
            this.save(operLog);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 分页查询操作日志
     *
     * @param operLogReqBo 查询参数
     * @return 分页结果
     */
    @Override
    public PageResponse<OperLogVO> pageList(OperLogReqBo operLogReqBo) {
        PageResponse<OperLogVO> pageResponse = new PageResponse<>();
        
        // 构建查询条件
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();
        
        // 模块标题
        if (StringUtils.hasText(operLogReqBo.getTitle())) {
            queryWrapper.like(SysOperLog::getTitle, operLogReqBo.getTitle());
        }
        
        // 业务类型
        if (operLogReqBo.getBusinessType() != null) {
            queryWrapper.eq(SysOperLog::getBusinessType, operLogReqBo.getBusinessType());
        }
        
        // 操作人员
        if (StringUtils.hasText(operLogReqBo.getOperName())) {
            queryWrapper.like(SysOperLog::getOperName, operLogReqBo.getOperName());
        }
        
        // 操作状态
        if (operLogReqBo.getStatus() != null) {
            queryWrapper.eq(SysOperLog::getStatus, operLogReqBo.getStatus());
        }
        
        // 时间范围
        if (operLogReqBo.getStartTime() != null) {
            queryWrapper.ge(SysOperLog::getOperTime, operLogReqBo.getStartTime());
        }
        if (operLogReqBo.getEndTime() != null) {
            queryWrapper.le(SysOperLog::getOperTime, operLogReqBo.getEndTime());
        }
        
        // 按操作时间倒序
        queryWrapper.orderByDesc(SysOperLog::getOperTime);
        
        // 分页查询
        Page<SysOperLog> page = new Page<>(operLogReqBo.getPageNum(), operLogReqBo.getPageSize());
        IPage<SysOperLog> pageList = this.page(page, queryWrapper);
        
        // 转换为VO
        List<OperLogVO> voList = pageList.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 设置分页结果
        pageResponse.setTotal(pageList.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(pageList.getCurrent());
        pageResponse.setPageSize(pageList.getSize());
        pageResponse.setPageCount(pageList.getPages());
        
        return pageResponse;
    }

    /**
     * 转换为VO
     *
     * @param operLog 操作日志实体
     * @return 操作日志VO
     */
    private OperLogVO convertToVO(SysOperLog operLog) {
        OperLogVO vo = new OperLogVO();
        BeanUtils.copyProperties(operLog, vo);
        return vo;
    }
}
