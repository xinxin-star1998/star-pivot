package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.domain.bo.SysNoticeVO;
import com.star.pivot.system.domain.dto.SysNoticeDTO;
import com.star.pivot.system.domain.dto.SysNoticeQueryDTO;
import com.star.pivot.system.domain.entity.SysNotice;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysNoticeMapper;
import com.star.pivot.system.service.interfaces.ISysNoticeService;
import com.star.pivot.system.utils.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 通知公告Service业务层实现
 * 
 * @author admin
 * @date 2026-02-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService
{

    /**
     * 分页查询通知公告列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<SysNoticeVO> selectSysNoticePage(SysNoticeQueryDTO queryDTO)
    {
        PageResponse<SysNoticeVO> pageResponse = new PageResponse<>();
        Page<SysNotice> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysNotice> sysNoticePage = baseMapper.selectPageList(page, queryDTO);
        
        // 转换为VO
        java.util.List<SysNoticeVO> voList = sysNoticePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
        
        pageResponse.setTotal(sysNoticePage.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(sysNoticePage.getCurrent());
        pageResponse.setPageSize(sysNoticePage.getSize());
        pageResponse.setPageCount(sysNoticePage.getPages());
        return pageResponse;
    }

    /**
     * 根据主键查询通知公告详细信息
     * 
     * @param noticeId 通知公告主键
     * @return 通知公告信息
     */
    @Override
    public SysNoticeVO selectSysNoticeByNoticeId(Integer noticeId)
    {
        SysNotice sysNotice = this.getById(noticeId);
        AssertUtils.notNull(sysNotice, ErrorCode.NOTICE_NOT_FOUND);
        return convertToVO(sysNotice);
    }

    /**
     * 新增通知公告
     * 
     * @param sysNoticeDTO 通知公告信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertSysNotice(SysNoticeDTO sysNoticeDTO)
    {
        SysNotice sysNotice = new SysNotice();
        BeanUtils.copyProperties(sysNoticeDTO, sysNotice);
        sysNotice.setCreateBy(Objects.requireNonNull(getCurrentUser()).getUserName());
        sysNotice.setCreateTime(java.time.LocalDateTime.now());
        return this.save(sysNotice);
    }

    /**
     * 修改通知公告
     * 
     * @param sysNoticeDTO 通知公告信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSysNotice(SysNoticeDTO sysNoticeDTO)
    {
        SysNotice sysNotice = this.getById(sysNoticeDTO.getNoticeId());
        AssertUtils.notNull(sysNotice, ErrorCode.NOTICE_NOT_FOUND);
        BeanUtils.copyProperties(sysNoticeDTO, sysNotice, "noticeId");
        sysNotice.setUpdateBy(Objects.requireNonNull(getCurrentUser()).getUserName());
        sysNotice.setUpdateTime(java.time.LocalDateTime.now());
        return this.updateById(sysNotice);
    }

    /**
     * 批量删除通知公告
     * 
     * @param noticeIds 需要删除的通知公告主键数组
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteSysNoticeByNoticeIds(List<Long> noticeIds)
    {
        return this.removeByIds(noticeIds);
    }

    /**
     * 转换为VO
     * 
     * @param sysNotice 实体对象
     * @return VO对象
     */
    private SysNoticeVO convertToVO(SysNotice sysNotice)
    {
        SysNoticeVO vo = new SysNoticeVO();
        BeanUtils.copyProperties(sysNotice, vo);
        return vo;
    }
    /**
     * 获取当前用户
     *
     * @return 当前用户，未登录或 principal 非 LoginUser 时返回 null
     */
    private SysUser getCurrentUser() {
        var info = SecurityContextUtils.getLoginUser();
        return (info instanceof LoginUser) ? ((LoginUser) info).getUser() : null;
    }
}
