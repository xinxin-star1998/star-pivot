package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.file.constant.FileAuditAction;
import com.star.pivot.file.domain.dto.SysFileAuditQueryDTO;
import com.star.pivot.file.domain.entity.SysFileAudit;
import com.star.pivot.file.domain.vo.SysFileAuditVo;
import com.star.pivot.file.mapper.SysFileAuditMapper;
import com.star.pivot.file.service.ISysFileAuditService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileAuditServiceImpl implements ISysFileAuditService {

    private final SysFileAuditMapper sysFileAuditMapper;

    @Override
    public void record(FileAuditAction action, Long fileId, String fileName, String detail) {
        record(action, fileId, fileName, detail, null);
    }

    @Override
    public void record(FileAuditAction action, Long fileId, String fileName, String detail, String operIp) {
        try {
            SysFileAudit audit = new SysFileAudit();
            audit.setFileId(fileId);
            audit.setFileName(fileName);
            audit.setAction(action.name());
            audit.setDetail(detail);
            audit.setOperBy(SecurityContextUtils.getUsername());
            audit.setOperByUserId(SecurityContextUtils.getUserId());
            audit.setOperIp(operIp);
            audit.setOperTime(LocalDateTime.now());
            sysFileAuditMapper.insert(audit);
        } catch (Exception e) {
            log.warn("写入文件审计失败 action={}, fileId={}: {}", action, fileId, e.getMessage());
        }
    }

    @Override
    public PageResponse<SysFileAuditVo> pageList(SysFileAuditQueryDTO queryDTO) {
        Page<SysFileAudit> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysFileAudit> result = sysFileAuditMapper.selectAuditPage(page, queryDTO);
        PageResponse<SysFileAuditVo> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setPageNum(result.getCurrent());
        response.setPageSize(result.getSize());
        response.setPageCount(result.getPages());
        response.setRows(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
        return response;
    }

    private SysFileAuditVo toVo(SysFileAudit audit) {
        SysFileAuditVo vo = new SysFileAuditVo();
        BeanUtils.copyProperties(audit, vo);
        try {
            vo.setActionLabel(FileAuditAction.valueOf(audit.getAction()).getLabel());
        } catch (Exception e) {
            vo.setActionLabel(audit.getAction());
        }
        return vo;
    }
}
