package com.star.pivot.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysUserMapper;
import com.star.pivot.workflow.constant.WorkflowConstants;
import com.star.pivot.workflow.domain.dto.TaskQueryDTO;
import com.star.pivot.workflow.domain.entity.WfInstance;
import com.star.pivot.workflow.domain.entity.WfProcessDef;
import com.star.pivot.workflow.domain.entity.WfTask;
import com.star.pivot.workflow.domain.entity.WfTaskHistory;
import com.star.pivot.workflow.domain.vo.InstanceHistoryVO;
import com.star.pivot.workflow.domain.vo.InstanceNodeStatusVO;
import com.star.pivot.workflow.domain.vo.InstanceProgressVO;
import com.star.pivot.workflow.domain.vo.TaskVO;
import com.star.pivot.workflow.mapper.WfInstanceMapper;
import com.star.pivot.workflow.mapper.WfProcessDefMapper;
import com.star.pivot.workflow.mapper.WfTaskHistoryMapper;
import com.star.pivot.workflow.mapper.WfTaskMapper;
import com.star.pivot.workflow.service.WfTaskQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WfTaskQueryServiceImpl implements WfTaskQueryService {

    @Autowired
    private WfTaskMapper taskMapper;

    @Autowired
    private WfInstanceMapper instanceMapper;

    @Autowired
    private WfProcessDefMapper processDefMapper;

    @Autowired
    private WfTaskHistoryMapper taskHistoryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public PageResponse<TaskVO> todoPage(TaskQueryDTO query) {
        Long userId = SecurityContextUtils.getUserId();
        LambdaQueryWrapper<WfTask> wrapper = buildTaskWrapper(query, userId, WorkflowConstants.TASK_PENDING);
        return pageTasks(query, wrapper);
    }

    @Override
    public PageResponse<TaskVO> donePage(TaskQueryDTO query) {
        Long userId = SecurityContextUtils.getUserId();
        LambdaQueryWrapper<WfTask> wrapper = buildTaskWrapper(query, userId, WorkflowConstants.TASK_COMPLETED);
        return pageTasks(query, wrapper);
    }

    private LambdaQueryWrapper<WfTask> buildTaskWrapper(TaskQueryDTO query, Long userId, String status) {
        LambdaQueryWrapper<WfTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfTask::getAssigneeId, userId)
                .eq(WfTask::getStatus, status);
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.apply("EXISTS (SELECT 1 FROM wf_instance i WHERE i.instance_id = t.instance_id AND i.title LIKE CONCAT('%', {0}, '%'))", query.getTitle());
        }
        if (StringUtils.hasText(query.getProcessCode())) {
            wrapper.apply("EXISTS (SELECT 1 FROM wf_instance i WHERE i.instance_id = t.instance_id AND i.process_code = {0})", query.getProcessCode());
        }
        if (WorkflowConstants.TASK_PENDING.equals(status)) {
            wrapper.orderByDesc(WfTask::getCreateTime);
        } else {
            wrapper.orderByDesc(WfTask::getFinishTime);
        }
        return wrapper;
    }

    @Override
    public PageResponse<TaskVO> startedPage(TaskQueryDTO query) {
        Long userId = SecurityContextUtils.getUserId();
        Page<WfInstance> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<WfInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfInstance::getStarterId, userId)
                .like(StringUtils.hasText(query.getTitle()), WfInstance::getTitle, query.getTitle())
                .eq(StringUtils.hasText(query.getProcessCode()), WfInstance::getProcessCode, query.getProcessCode())
                .orderByDesc(WfInstance::getCreateTime);
        Page<WfInstance> result = instanceMapper.selectPage(page, wrapper);

        Map<Long, String> userNameMap = loadUserNames(Set.of(userId));
        List<TaskVO> rows = new ArrayList<>();
        for (WfInstance instance : result.getRecords()) {
            TaskVO vo = new TaskVO();
            vo.setInstanceId(instance.getInstanceId());
            vo.setProcessCode(instance.getProcessCode());
            vo.setProcessName(instance.getProcessName());
            vo.setBusinessKey(instance.getBusinessKey());
            vo.setTitle(instance.getTitle());
            vo.setStarterId(instance.getStarterId());
            vo.setStarterName(userNameMap.get(instance.getStarterId()));
            vo.setStatus(instance.getStatus());
            vo.setCreateTime(instance.getCreateTime());
            rows.add(vo);
        }

        PageResponse<TaskVO> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setPageNum(result.getCurrent());
        response.setPageSize(result.getSize());
        response.setRows(rows);
        return response;
    }

    private PageResponse<TaskVO> pageTasks(TaskQueryDTO query, LambdaQueryWrapper<WfTask> wrapper) {
        Page<WfTask> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<WfTask> result = taskMapper.selectPageWithInstance(page, wrapper);
        if (result.getRecords().isEmpty()) {
            PageResponse<TaskVO> empty = new PageResponse<>();
            empty.setTotal(0L);
            empty.setPageNum(result.getCurrent());
            empty.setPageSize(result.getSize());
            empty.setRows(List.of());
            return empty;
        }

        Set<Long> instanceIds = result.getRecords().stream().map(WfTask::getInstanceId).collect(Collectors.toSet());
        List<WfInstance> instances = instanceMapper.selectBatchIds(instanceIds);
        Map<Long, WfInstance> instanceMap = instances.stream()
                .collect(Collectors.toMap(WfInstance::getInstanceId, i -> i));

        Set<Long> userIds = instances.stream().map(WfInstance::getStarterId).collect(Collectors.toSet());
        result.getRecords().forEach(task -> userIds.add(task.getAssigneeId()));
        Map<Long, String> userNameMap = loadUserNames(userIds);

        List<TaskVO> rows = result.getRecords().stream()
                .map(task -> {
                    WfInstance instance = instanceMap.get(task.getInstanceId());
                    if (instance == null) {
                        return null;
                    }
                    TaskVO vo = new TaskVO();
                    vo.setTaskId(task.getTaskId());
                    vo.setInstanceId(task.getInstanceId());
                    vo.setProcessCode(instance.getProcessCode());
                    vo.setProcessName(instance.getProcessName());
                    vo.setBusinessKey(instance.getBusinessKey());
                    vo.setTitle(instance.getTitle());
                    vo.setNodeId(task.getNodeId());
                    vo.setNodeName(task.getNodeName());
                    vo.setAssigneeId(task.getAssigneeId());
                    vo.setAssigneeName(userNameMap.get(task.getAssigneeId()));
                    vo.setStarterId(instance.getStarterId());
                    vo.setStarterName(userNameMap.get(instance.getStarterId()));
                    vo.setStatus(task.getStatus());
                    vo.setCreateTime(task.getCreateTime());
                    return vo;
                })
                .filter(Objects::nonNull)
                .toList();

        PageResponse<TaskVO> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setPageNum(result.getCurrent());
        response.setPageSize(result.getSize());
        response.setRows(rows);
        return response;
    }

    @Override
    public InstanceProgressVO getInstanceProgress(Long instanceId) {
        WfInstance instance = instanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程实例不存在");
        }

        WfProcessDef processDef = processDefMapper.selectById(instance.getDefId());
        if (processDef == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
        }

        List<WfTaskHistory> histories = taskHistoryMapper.selectList(new LambdaQueryWrapper<WfTaskHistory>()
                .eq(WfTaskHistory::getInstanceId, instanceId)
                .orderByAsc(WfTaskHistory::getCreateTime));

        Set<Long> operatorIds = histories.stream()
                .map(WfTaskHistory::getOperatorId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = loadUserNames(operatorIds);

        Map<String, String> nodeStatusMap = buildNodeStatusMap(instance, processDef.getDefJson(), histories);

        InstanceProgressVO vo = new InstanceProgressVO();
        vo.setInstanceId(instance.getInstanceId());
        vo.setTitle(instance.getTitle());
        vo.setProcessCode(instance.getProcessCode());
        vo.setProcessName(instance.getProcessName());
        vo.setStatus(instance.getStatus());
        vo.setCurrentNodeId(instance.getCurrentNodeId());
        vo.setDefJson(processDef.getDefJson());
        vo.setCreateTime(instance.getCreateTime());
        vo.setFinishTime(instance.getFinishTime());
        vo.setNodeStatuses(nodeStatusMap.entrySet().stream().map(entry -> {
            InstanceNodeStatusVO nodeStatus = new InstanceNodeStatusVO();
            nodeStatus.setNodeId(entry.getKey());
            nodeStatus.setStatus(entry.getValue());
            return nodeStatus;
        }).toList());
        vo.setHistories(histories.stream().map(h -> {
            InstanceHistoryVO historyVO = new InstanceHistoryVO();
            historyVO.setHistoryId(h.getHistoryId());
            historyVO.setNodeId(h.getNodeId());
            historyVO.setNodeName(h.getNodeName());
            historyVO.setAction(h.getAction());
            historyVO.setComment(h.getComment());
            historyVO.setOperatorId(h.getOperatorId());
            historyVO.setOperatorName(userNameMap.get(h.getOperatorId()));
            historyVO.setCreateTime(h.getCreateTime());
            return historyVO;
        }).toList());
        return vo;
    }

    private Map<String, String> buildNodeStatusMap(WfInstance instance, String defJson, List<WfTaskHistory> histories) {
        Map<String, String> statusMap = new LinkedHashMap<>();
        Set<String> allNodeIds = parseNodeIds(defJson);
        Set<String> completed = new HashSet<>();
        String rejectedNodeId = null;

        for (WfTaskHistory history : histories) {
            if (!StringUtils.hasText(history.getNodeId())) {
                continue;
            }
            if (WorkflowConstants.ACTION_START.equals(history.getAction())
                    || WorkflowConstants.ACTION_APPROVE.equals(history.getAction())) {
                completed.add(history.getNodeId());
            }
            if (WorkflowConstants.ACTION_REJECT.equals(history.getAction())) {
                rejectedNodeId = history.getNodeId();
            }
        }

        if (WorkflowConstants.INSTANCE_APPROVED.equals(instance.getStatus())) {
            completed.addAll(parseEndNodeIds(defJson));
        }

        for (String nodeId : allNodeIds) {
            if (rejectedNodeId != null && rejectedNodeId.equals(nodeId)) {
                statusMap.put(nodeId, "REJECTED");
            } else if (WorkflowConstants.INSTANCE_RUNNING.equals(instance.getStatus())
                    && nodeId.equals(instance.getCurrentNodeId())) {
                statusMap.put(nodeId, "CURRENT");
            } else if (completed.contains(nodeId)) {
                statusMap.put(nodeId, "COMPLETED");
            } else {
                statusMap.put(nodeId, "PENDING");
            }
        }
        return statusMap;
    }

    private Set<String> parseNodeIds(String defJson) {
        return parseNodeIdsByType(defJson, null);
    }

    private Set<String> parseEndNodeIds(String defJson) {
        return parseNodeIdsByType(defJson, "end");
    }

    private Set<String> parseNodeIdsByType(String defJson, String filterType) {
        Set<String> ids = filterType == null ? new LinkedHashSet<>() : new HashSet<>();
        if (!StringUtils.hasText(defJson)) {
            return ids;
        }
        try {
            JSONObject root = JSON.parseObject(defJson);
            JSONArray nodes = root.getJSONArray("nodes");
            if (nodes == null) {
                return ids;
            }
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                if (filterType != null && !filterType.equals(node.getString("type"))) {
                    continue;
                }
                String id = node.getString("id");
                if (StringUtils.hasText(id)) {
                    ids.add(id);
                }
            }
        } catch (Exception ignored) {
            // ignore malformed defJson
        }
        return ids;
    }

    private Map<Long, String> loadUserNames(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, String> map = new HashMap<>();
        for (SysUser user : users) {
            map.put(user.getUserId(), StringUtils.hasText(user.getNickName()) ? user.getNickName() : user.getUserName());
        }
        return map;
    }
}
