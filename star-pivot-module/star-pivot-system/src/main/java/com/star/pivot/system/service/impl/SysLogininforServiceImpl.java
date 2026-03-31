package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.LogininforReqBo;
import com.star.pivot.system.domain.bo.LogininforVO;
import com.star.pivot.system.domain.entity.SysLogininfor;
import com.star.pivot.system.mapper.SysLogininforMapper;
import com.star.pivot.system.service.interfaces.SysLogininforService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现类
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Slf4j
@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements SysLogininforService {

    /**
     * 保存登录日志
     * 使用异步方式保存，避免影响主流程性能
     *
     * @param logininfor 登录日志
     */
    @Override
    @Async
    public void saveLogininfor(SysLogininfor logininfor) {
        try {
            this.save(logininfor);
        } catch (Exception e) {
            log.error("保存登录日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 分页查询登录日志
     *
     * @param logininforReqBo 查询参数
     * @return 分页结果
     */
    @Override
    public PageResponse<LogininforVO> pageList(LogininforReqBo logininforReqBo) {
        PageResponse<LogininforVO> pageResponse = new PageResponse<>();
        
        // 构建查询条件
        LambdaQueryWrapper<SysLogininfor> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户账号
        if (StringUtils.hasText(logininforReqBo.getUserName())) {
            queryWrapper.like(SysLogininfor::getUserName, logininforReqBo.getUserName());
        }
        
        // 登录IP地址
        if (StringUtils.hasText(logininforReqBo.getIpaddr())) {
            queryWrapper.like(SysLogininfor::getIpaddr, logininforReqBo.getIpaddr());
        }
        
        // 登录状态
        if (StringUtils.hasText(logininforReqBo.getStatus())) {
            queryWrapper.eq(SysLogininfor::getStatus, logininforReqBo.getStatus());
        }
        
        // 时间范围
        if (logininforReqBo.getStartTime() != null) {
            queryWrapper.ge(SysLogininfor::getLoginTime, logininforReqBo.getStartTime());
        }
        if (logininforReqBo.getEndTime() != null) {
            queryWrapper.le(SysLogininfor::getLoginTime, logininforReqBo.getEndTime());
        }
        
        // 按登录时间倒序
        queryWrapper.orderByDesc(SysLogininfor::getLoginTime);
        
        // 分页查询
        Page<SysLogininfor> page = new Page<>(logininforReqBo.getPageNum(), logininforReqBo.getPageSize());
        IPage<SysLogininfor> pageList = this.page(page, queryWrapper);
        
        // 转换为VO
        List<LogininforVO> voList = pageList.getRecords().stream()
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
     * @param logininfor 登录日志实体
     * @return 登录日志VO
     */
    private LogininforVO convertToVO(SysLogininfor logininfor) {
        LogininforVO vo = new LogininforVO();
        BeanUtils.copyProperties(logininfor, vo);
        return vo;
    }
}
