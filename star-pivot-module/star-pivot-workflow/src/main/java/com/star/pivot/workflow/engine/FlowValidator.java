package com.star.pivot.workflow.engine;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.workflow.domain.model.SpfDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FlowValidator {

    private static final Set<String> SUPPORTED_TYPES = Set.of("start", "end", "approval", "condition");

    public void validate(SpfDefinition def) {
        if (def == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程定义不能为空");
        }
        if (!StringUtils.hasText(def.getProcessCode())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程编码不能为空");
        }
        if (!StringUtils.hasText(def.getProcessName())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程名称不能为空");
        }
        if (CollectionUtils.isEmpty(def.getNodes())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程节点不能为空");
        }

        List<SpfDefinition.SpfNode> starts = def.getNodes().stream()
                .filter(n -> "start".equals(n.getType()))
                .toList();
        if (starts.size() != 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程必须有且仅有一个开始节点");
        }

        long endCount = def.getNodes().stream().filter(n -> "end".equals(n.getType())).count();
        if (endCount < 1) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程至少需要一个结束节点");
        }

        Set<String> nodeIds = new HashSet<>();
        for (SpfDefinition.SpfNode node : def.getNodes()) {
            if (!StringUtils.hasText(node.getId()) || !nodeIds.add(node.getId())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "节点 ID 不能为空且不能重复");
            }
            if (!SUPPORTED_TYPES.contains(node.getType())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "不支持的节点类型: " + node.getType());
            }
            if ("approval".equals(node.getType())) {
                validateApprovalNode(node);
            }
        }

        Map<String, List<SpfDefinition.SpfEdge>> outEdges = buildOutEdges(def.getEdges());
        validateReachable(starts.get(0).getId(), nodeIds, outEdges);
        validateNoCycle(def.getNodes(), outEdges);

        for (SpfDefinition.SpfNode node : def.getNodes()) {
            if ("condition".equals(node.getType())) {
                validateConditionNode(node.getId(), outEdges.getOrDefault(node.getId(), List.of()));
            }
        }
    }

    private void validateApprovalNode(SpfDefinition.SpfNode node) {
        if (node.getData() == null || node.getData().get("assigneeRule") == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "审批节点必须配置审批人规则: " + node.getId());
        }
    }

    private void validateConditionNode(String nodeId, List<SpfDefinition.SpfEdge> edges) {
        if (edges.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "条件节点至少有一条出线: " + nodeId);
        }
        boolean hasDefault = edges.stream().anyMatch(e -> isDefaultEdge(e));
        if (!hasDefault) {
            throw new BizException(ErrorCode.PARAM_INVALID, "条件节点必须有一条默认分支: " + nodeId);
        }
    }

    private boolean isDefaultEdge(SpfDefinition.SpfEdge edge) {
        if (edge.getData() == null || edge.getData().get("condition") == null) {
            return false;
        }
        Object condition = edge.getData().get("condition");
        if (condition instanceof Map<?, ?> map) {
            return "default".equals(String.valueOf(map.get("type")));
        }
        return false;
    }

    private Map<String, List<SpfDefinition.SpfEdge>> buildOutEdges(List<SpfDefinition.SpfEdge> edges) {
        if (CollectionUtils.isEmpty(edges)) {
            return Map.of();
        }
        return edges.stream().collect(Collectors.groupingBy(SpfDefinition.SpfEdge::getSource));
    }

    private void validateReachable(String startId, Set<String> allNodeIds,
                                   Map<String, List<SpfDefinition.SpfEdge>> outEdges) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(startId);
        visited.add(startId);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (SpfDefinition.SpfEdge edge : outEdges.getOrDefault(current, List.of())) {
                if (visited.add(edge.getTarget())) {
                    queue.add(edge.getTarget());
                }
            }
        }
        if (visited.size() != allNodeIds.size()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "存在无法从开始节点到达的孤立节点");
        }
    }

    private void validateNoCycle(List<SpfDefinition.SpfNode> nodes,
                                 Map<String, List<SpfDefinition.SpfEdge>> outEdges) {
        Map<String, Integer> indegree = new HashMap<>();
        for (SpfDefinition.SpfNode node : nodes) {
            indegree.put(node.getId(), 0);
        }
        for (List<SpfDefinition.SpfEdge> list : outEdges.values()) {
            for (SpfDefinition.SpfEdge edge : list) {
                indegree.merge(edge.getTarget(), 1, Integer::sum);
            }
        }
        Queue<String> queue = indegree.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayDeque::new));
        int visited = 0;
        while (!queue.isEmpty()) {
            String id = queue.poll();
            visited++;
            for (SpfDefinition.SpfEdge edge : outEdges.getOrDefault(id, List.of())) {
                int next = indegree.merge(edge.getTarget(), -1, Integer::sum);
                if (next == 0) {
                    queue.add(edge.getTarget());
                }
            }
        }
        if (visited != nodes.size()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "流程不允许存在环路");
        }
    }
}
