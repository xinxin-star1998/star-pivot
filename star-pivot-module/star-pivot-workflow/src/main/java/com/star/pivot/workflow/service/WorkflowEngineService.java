package com.star.pivot.workflow.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.workflow.constant.WorkflowConstants;
import com.star.pivot.workflow.domain.dto.StartWorkflowDTO;
import com.star.pivot.workflow.domain.dto.TaskActionDTO;
import com.star.pivot.workflow.domain.entity.WfInstance;
import com.star.pivot.workflow.domain.entity.WfProcessDef;
import com.star.pivot.workflow.domain.entity.WfTask;
import com.star.pivot.workflow.domain.entity.WfTaskHistory;
import com.star.pivot.workflow.domain.model.RuntimeDefinition;
import com.star.pivot.workflow.engine.AssigneeResolver;
import com.star.pivot.workflow.engine.ConditionEvaluator;
import com.star.pivot.workflow.listener.WorkflowCompletedListener;
import com.star.pivot.workflow.mapper.WfInstanceMapper;
import com.star.pivot.workflow.mapper.WfProcessDefMapper;
import com.star.pivot.workflow.mapper.WfTaskHistoryMapper;
import com.star.pivot.workflow.mapper.WfTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowEngineService {

    @Autowired
    private WfProcessDefMapper processDefMapper;

    @Autowired
    private WfInstanceMapper instanceMapper;

    @Autowired
    private WfTaskMapper taskMapper;

    @Autowired
    private WfTaskHistoryMapper historyMapper;

    @Autowired
    private AssigneeResolver assigneeResolver;

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    @Autowired
    private List<WorkflowCompletedListener> completedListeners;

    @Transactional(rollbackFor = Exception.class)
    public Long start(StartWorkflowDTO dto) {
        Long starterId = SecurityContextUtils.getUserId();
        if (starterId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录无法发起流程");
        }

        WfProcessDef processDef = loadPublishedDef(dto.getProcessCode());
        RuntimeDefinition runtime = parseRuntime(processDef.getRuntimeJson());

        long runningCount = instanceMapper.selectCount(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getBusinessKey, dto.getBusinessKey())
                .eq(WfInstance::getStatus, WorkflowConstants.INSTANCE_RUNNING));
        if (runningCount > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "该业务已存在进行中的流程");
        }

        WfInstance instance = new WfInstance();
        instance.setDefId(processDef.getDefId());
        instance.setProcessCode(processDef.getProcessCode());
        instance.setProcessName(processDef.getProcessName());
        instance.setBusinessKey(dto.getBusinessKey());
        instance.setTitle(dto.getTitle());
        instance.setStarterId(starterId);
        instance.setStatus(WorkflowConstants.INSTANCE_RUNNING);
        instance.setVariablesJson(JSON.toJSONString(dto.getVariables() != null ? dto.getVariables() : Map.of()));
        instance.setCreateTime(LocalDateTime.now());
        instanceMapper.insert(instance);

        saveHistory(instance.getInstanceId(), null, runtime.getStartNodeId(), "开始",
                starterId, WorkflowConstants.ACTION_START, "发起流程");

        enterNode(instance, runtime, runtime.getStartNodeId());
        return instance.getInstanceId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(TaskActionDTO dto) {
        WfTask task = loadPendingTask(dto.getTaskId());
        Long operatorId = requireOperator();
        if (!operatorId.equals(task.getAssigneeId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权审批该任务");
        }

        WfInstance instance = loadRunningInstance(task.getInstanceId());
        RuntimeDefinition runtime = loadRuntimeByInstance(instance);

        completeTask(task, WorkflowConstants.ACTION_APPROVE, dto.getComment());

        RuntimeDefinition.RuntimeNode currentNode = runtime.getNodeMap().get(task.getNodeId());
        String approveMode = currentNode != null && StringUtils.hasText(currentNode.getApproveMode())
                ? currentNode.getApproveMode() : WorkflowConstants.APPROVE_MODE_OR;

        if (WorkflowConstants.APPROVE_MODE_AND.equals(approveMode)) {
            long pending = taskMapper.selectCount(new LambdaQueryWrapper<WfTask>()
                    .eq(WfTask::getInstanceId, instance.getInstanceId())
                    .eq(WfTask::getNodeId, task.getNodeId())
                    .eq(WfTask::getStatus, WorkflowConstants.TASK_PENDING));
            if (pending > 0) {
                return;
            }
        } else {
            cancelSiblingTasks(instance.getInstanceId(), task.getNodeId(), task.getTaskId());
        }

        moveNext(instance, runtime, task.getNodeId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void reject(TaskActionDTO dto) {
        WfTask task = loadPendingTask(dto.getTaskId());
        Long operatorId = requireOperator();
        if (!operatorId.equals(task.getAssigneeId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权审批该任务");
        }

        WfInstance instance = loadRunningInstance(task.getInstanceId());
        completeTask(task, WorkflowConstants.ACTION_REJECT, dto.getComment());
        cancelAllPendingTasks(instance.getInstanceId());

        LambdaUpdateWrapper<WfInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WfInstance::getInstanceId, instance.getInstanceId())
                .set(WfInstance::getStatus, WorkflowConstants.INSTANCE_REJECTED)
                .set(WfInstance::getCurrentNodeId, null)
                .set(WfInstance::getFinishTime, LocalDateTime.now());
        instanceMapper.update(null, updateWrapper);

        notifyRejected(instance, dto.getComment());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long instanceId) {
        Long operatorId = requireOperator();
        WfInstance instance = loadRunningInstance(instanceId);
        if (!operatorId.equals(instance.getStarterId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "仅发起人可撤销流程");
        }

        String currentNodeId = instance.getCurrentNodeId();
        cancelAllPendingTasks(instanceId);
        
        LambdaUpdateWrapper<WfInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WfInstance::getInstanceId, instanceId)
                .set(WfInstance::getStatus, WorkflowConstants.INSTANCE_CANCELLED)
                .set(WfInstance::getCurrentNodeId, null)
                .set(WfInstance::getFinishTime, LocalDateTime.now());
        instanceMapper.update(null, updateWrapper);

        saveHistory(instanceId, null, currentNodeId, "撤销",
                operatorId, WorkflowConstants.ACTION_CANCEL, "发起人撤销");
    }

    private void enterNode(WfInstance instance, RuntimeDefinition runtime, String nodeId) {
        RuntimeDefinition.RuntimeNode node = runtime.getNodeMap().get(nodeId);
        if (node == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程节点不存在: " + nodeId);
        }

        switch (node.getType()) {
            case "start" -> {
                String next = firstNext(node);
                if (!StringUtils.hasText(next)) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "开始节点未配置后续节点");
                }
                enterNode(instance, runtime, next);
            }
            case "approval" -> createApprovalTasks(instance, nodeId, node);
            case "condition" -> {
                Map<String, Object> variables = parseVariables(instance.getVariablesJson());
                String next = conditionEvaluator.evaluate(node.getBranches(), variables);
                if (!StringUtils.hasText(next)) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "条件节点未匹配到后续分支");
                }
                enterNode(instance, runtime, next);
            }
            case "end" -> finishApproved(instance);
            default -> throw new BizException(ErrorCode.PARAM_INVALID, "不支持的节点类型: " + node.getType());
        }
    }

    private void moveNext(WfInstance instance, RuntimeDefinition runtime, String currentNodeId) {
        RuntimeDefinition.RuntimeNode node = runtime.getNodeMap().get(currentNodeId);
        String next = firstNext(node);
        if (!StringUtils.hasText(next)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "当前节点未配置后续节点");
        }
        enterNode(instance, runtime, next);
    }

    private void createApprovalTasks(WfInstance instance, String nodeId, RuntimeDefinition.RuntimeNode node) {
        List<Long> assignees = assigneeResolver.resolve(node.getAssigneeRule(), instance.getStarterId());
        instance.setCurrentNodeId(nodeId);
        instanceMapper.updateById(instance);

        LocalDateTime now = LocalDateTime.now();
        assignees.forEach(assigneeId -> {
            WfTask task = new WfTask();
            task.setInstanceId(instance.getInstanceId());
            task.setNodeId(nodeId);
            task.setNodeName(node.getName());
            task.setAssigneeId(assigneeId);
            task.setStatus(WorkflowConstants.TASK_PENDING);
            task.setCreateTime(now);
            taskMapper.insert(task);
        });
    }

    private void finishApproved(WfInstance instance) {
        LambdaUpdateWrapper<WfInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WfInstance::getInstanceId, instance.getInstanceId())
                .set(WfInstance::getStatus, WorkflowConstants.INSTANCE_APPROVED)
                .set(WfInstance::getCurrentNodeId, null)
                .set(WfInstance::getFinishTime, LocalDateTime.now());
        instanceMapper.update(null, updateWrapper);
        notifyApproved(instance);
    }

    private void completeTask(WfTask task, String action, String comment) {
        task.setStatus(WorkflowConstants.TASK_COMPLETED);
        task.setAction(action);
        task.setComment(comment);
        task.setFinishTime(LocalDateTime.now());
        taskMapper.updateById(task);

        saveHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
                requireOperator(), action, comment);
    }

    private void cancelSiblingTasks(Long instanceId, String nodeId, Long keepTaskId) {
        taskMapper.update(null, new LambdaUpdateWrapper<WfTask>()
                .eq(WfTask::getInstanceId, instanceId)
                .eq(WfTask::getNodeId, nodeId)
                .eq(WfTask::getStatus, WorkflowConstants.TASK_PENDING)
                .ne(WfTask::getTaskId, keepTaskId)
                .set(WfTask::getStatus, WorkflowConstants.TASK_CANCELLED)
                .set(WfTask::getFinishTime, LocalDateTime.now()));
    }

    private void cancelAllPendingTasks(Long instanceId) {
        taskMapper.update(null, new LambdaUpdateWrapper<WfTask>()
                .eq(WfTask::getInstanceId, instanceId)
                .eq(WfTask::getStatus, WorkflowConstants.TASK_PENDING)
                .set(WfTask::getStatus, WorkflowConstants.TASK_CANCELLED)
                .set(WfTask::getFinishTime, LocalDateTime.now()));
    }

    private void saveHistory(Long instanceId, Long taskId, String nodeId, String nodeName,
                             Long operatorId, String action, String comment) {
        WfTaskHistory history = new WfTaskHistory();
        history.setInstanceId(instanceId);
        history.setTaskId(taskId);
        history.setNodeId(nodeId != null ? nodeId : "");
        history.setNodeName(nodeName != null ? nodeName : "");
        history.setOperatorId(operatorId);
        history.setAction(action);
        history.setComment(comment);
        history.setCreateTime(LocalDateTime.now());
        historyMapper.insert(history);
    }

    private void notifyApproved(WfInstance instance) {
        if (CollectionUtils.isEmpty(completedListeners)) {
            return;
        }
        notifyAsync(() -> {
            for (WorkflowCompletedListener listener : completedListeners) {
                if (listener.supports(instance.getProcessCode())) {
                    listener.onApproved(instance.getBusinessKey(), instance.getInstanceId());
                }
            }
        });
    }

    private void notifyRejected(WfInstance instance, String comment) {
        if (CollectionUtils.isEmpty(completedListeners)) {
            return;
        }
        notifyAsync(() -> {
            for (WorkflowCompletedListener listener : completedListeners) {
                if (listener.supports(instance.getProcessCode())) {
                    listener.onRejected(instance.getBusinessKey(), instance.getInstanceId(), comment);
                }
            }
        });
    }

    @org.springframework.scheduling.annotation.Async
    protected void notifyAsync(Runnable task) {
        task.run();
    }

    @org.springframework.cache.annotation.Cacheable(cacheNames = "workflowDef", key = "#processCode")
    public WfProcessDef loadPublishedDef(String processCode) {
        WfProcessDef def = processDefMapper.selectOne(new LambdaQueryWrapper<WfProcessDef>()
                .eq(WfProcessDef::getProcessCode, processCode)
                .eq(WfProcessDef::getStatus, WorkflowConstants.DEF_STATUS_PUBLISHED)
                .orderByDesc(WfProcessDef::getVersion)
                .last("LIMIT 1"));
        if (def == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "未找到已发布的流程定义: " + processCode);
        }
        return def;
    }

    private RuntimeDefinition loadRuntimeByInstance(WfInstance instance) {
        WfProcessDef def = processDefMapper.selectById(instance.getDefId());
        if (def == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不存在");
        }
        return parseRuntime(def.getRuntimeJson());
    }

    private RuntimeDefinition parseRuntime(String runtimeJson) {
        if (!StringUtils.hasText(runtimeJson)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程运行定义为空");
        }
        return JSON.parseObject(runtimeJson, RuntimeDefinition.class);
    }

    private Map<String, Object> parseVariables(String variablesJson) {
        if (!StringUtils.hasText(variablesJson)) {
            return new HashMap<>();
        }
        return JSON.parseObject(variablesJson, new TypeReference<Map<String, Object>>() {
        });
    }

    private WfTask loadPendingTask(Long taskId) {
        WfTask task = taskMapper.selectById(taskId);
        if (task == null || !WorkflowConstants.TASK_PENDING.equals(task.getStatus())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "任务不存在或已处理");
        }
        return task;
    }

    private WfInstance loadRunningInstance(Long instanceId) {
        WfInstance instance = instanceMapper.selectById(instanceId);
        if (instance == null || !WorkflowConstants.INSTANCE_RUNNING.equals(instance.getStatus())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程实例不存在或已结束");
        }
        return instance;
    }

    private Long requireOperator() {
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return userId;
    }

    private String firstNext(RuntimeDefinition.RuntimeNode node) {
        if (node == null || CollectionUtils.isEmpty(node.getNext())) {
            return null;
        }
        return node.getNext().get(0);
    }
}
