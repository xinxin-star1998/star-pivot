package com.star.pivot.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.workflow.constant.WorkflowConstants;
import com.star.pivot.workflow.domain.dto.ProcessDefQueryDTO;
import com.star.pivot.workflow.domain.dto.ProcessDefSaveDTO;
import com.star.pivot.workflow.domain.entity.WfProcessDef;
import com.star.pivot.workflow.domain.model.SpfDefinition;
import com.star.pivot.workflow.domain.vo.ProcessDefVO;
import com.star.pivot.workflow.engine.FlowCompiler;
import com.star.pivot.workflow.engine.FlowValidator;
import com.star.pivot.workflow.mapper.WfProcessDefMapper;
import com.star.pivot.workflow.service.WfProcessDefService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WfProcessDefServiceImpl extends ServiceImpl<WfProcessDefMapper, WfProcessDef> implements WfProcessDefService {

    @Autowired
    private FlowValidator flowValidator;

    @Autowired
    private FlowCompiler flowCompiler;

    @Override
    public PageResponse<ProcessDefVO> page(ProcessDefQueryDTO query) {
        Page<WfProcessDef> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<WfProcessDef> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getProcessCode()), WfProcessDef::getProcessCode, query.getProcessCode())
                .like(StringUtils.hasText(query.getProcessName()), WfProcessDef::getProcessName, query.getProcessName())
                .eq(StringUtils.hasText(query.getBizModule()), WfProcessDef::getBizModule, query.getBizModule())
                .eq(StringUtils.hasText(query.getStatus()), WfProcessDef::getStatus, query.getStatus())
                .orderByDesc(WfProcessDef::getUpdateTime);

        IPage<WfProcessDef> result = baseMapper.selectPage(page, wrapper);
        List<ProcessDefVO> rows = new ArrayList<>();
        for (WfProcessDef def : result.getRecords()) {
            rows.add(toVo(def));
        }
        PageResponse<ProcessDefVO> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setPageNum(result.getCurrent());
        response.setPageSize(result.getSize());
        response.setRows(rows);
        return response;
    }

    @Override
    public ProcessDefVO getById(Long defId) {
        WfProcessDef def = baseMapper.selectById(defId);
        if (def == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
        }
        return toVo(def);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(ProcessDefSaveDTO dto) {
        SpfDefinition spf = parseDefinition(dto.getDefJson());
        spf.setProcessCode(dto.getProcessCode());
        spf.setProcessName(dto.getProcessName());
        spf.setBizModule(dto.getBizModule());
        flowValidator.validate(spf);

        String operator = currentUsername();
        LocalDateTime now = LocalDateTime.now();
        if (dto.getDefId() != null) {
            WfProcessDef existing = baseMapper.selectById(dto.getDefId());
            if (existing == null) {
                throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
            }
            if (WorkflowConstants.DEF_STATUS_PUBLISHED.equals(existing.getStatus())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "已发布流程请新建版本，不能直接修改");
            }
            existing.setProcessCode(dto.getProcessCode());
            existing.setProcessName(dto.getProcessName());
            existing.setBizModule(dto.getBizModule());
            existing.setDefJson(JSON.toJSONString(spf));
            existing.setRemark(dto.getRemark());
            existing.setUpdateBy(operator);
            existing.setUpdateTime(now);
            this.updateById(existing);
            return existing.getDefId();
        }

        Integer maxVersion = baseMapper.selectMaxVersion(dto.getProcessCode());

        WfProcessDef def = new WfProcessDef();
        def.setProcessCode(dto.getProcessCode());
        def.setProcessName(dto.getProcessName());
        def.setBizModule(dto.getBizModule());
        def.setVersion(maxVersion + 1);
        def.setDefJson(JSON.toJSONString(spf));
        def.setStatus(WorkflowConstants.DEF_STATUS_DRAFT);
        def.setRemark(dto.getRemark());
        def.setCreateBy(operator);
        def.setUpdateBy(operator);
        def.setCreateTime(now);
        def.setUpdateTime(now);
        this.save(def);
        return def.getDefId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "workflowDef", allEntries = true)
    public void publish(Long defId) {
        WfProcessDef def = baseMapper.selectById(defId);
        if (def == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
        }
        SpfDefinition spf = parseDefinition(def.getDefJson());
        flowValidator.validate(spf);
        String runtimeJson = flowCompiler.compileToJson(spf);

        LambdaUpdateWrapper<WfProcessDef> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WfProcessDef::getProcessCode, def.getProcessCode())
                .eq(WfProcessDef::getStatus, WorkflowConstants.DEF_STATUS_PUBLISHED)
                .set(WfProcessDef::getStatus, WorkflowConstants.DEF_STATUS_DISABLED)
                .set(WfProcessDef::getUpdateBy, currentUsername())
                .set(WfProcessDef::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);

        LambdaUpdateWrapper<WfProcessDef> defUpdateWrapper = new LambdaUpdateWrapper<>();
        defUpdateWrapper.eq(WfProcessDef::getDefId, defId)
                .set(WfProcessDef::getRuntimeJson, runtimeJson)
                .set(WfProcessDef::getStatus, WorkflowConstants.DEF_STATUS_PUBLISHED)
                .set(WfProcessDef::getUpdateBy, currentUsername())
                .set(WfProcessDef::getUpdateTime, LocalDateTime.now());
        this.update(defUpdateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "workflowDef", allEntries = true)
    public void disable(Long defId) {
        LambdaUpdateWrapper<WfProcessDef> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WfProcessDef::getDefId, defId)
                .set(WfProcessDef::getStatus, WorkflowConstants.DEF_STATUS_DISABLED)
                .set(WfProcessDef::getUpdateBy, currentUsername())
                .set(WfProcessDef::getUpdateTime, LocalDateTime.now());
        boolean updated = this.update(updateWrapper);
        if (!updated) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "workflowDef", allEntries = true)
    public void removeByIds(List<Long> defIds) {
        if (defIds == null || defIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        for (Long defId : defIds) {
            WfProcessDef def = baseMapper.selectById(defId);
            if (def != null && WorkflowConstants.DEF_STATUS_PUBLISHED.equals(def.getStatus())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "已发布流程不能删除，请先停用");
            }
        }
        this.removeByIds(defIds);
    }

    private SpfDefinition parseDefinition(String defJson) {
        if (!StringUtils.hasText(defJson)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义 JSON 不能为空");
        }
        return JSON.parseObject(defJson, SpfDefinition.class);
    }

    private ProcessDefVO toVo(WfProcessDef def) {
        ProcessDefVO vo = new ProcessDefVO();
        BeanUtils.copyProperties(def, vo);
        return vo;
    }

    private String currentUsername() {
        String username = SecurityContextUtils.getUsername();
        return StringUtils.hasText(username) ? username : "system";
    }
}
