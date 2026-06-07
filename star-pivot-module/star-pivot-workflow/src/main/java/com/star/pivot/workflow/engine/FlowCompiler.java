package com.star.pivot.workflow.engine;

import com.alibaba.fastjson2.JSON;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.workflow.domain.model.RuntimeDefinition;
import com.star.pivot.workflow.domain.model.SpfDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FlowCompiler {

    public RuntimeDefinition compile(SpfDefinition def) {
        RuntimeDefinition runtime = new RuntimeDefinition();
        runtime.setSchemaVersion("1.0");
        runtime.setProcessCode(def.getProcessCode());

        SpfDefinition.SpfNode start = def.getNodes().stream()
                .filter(n -> "start".equals(n.getType()))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.PARAM_INVALID, "流程定义缺少开始节点"));
        runtime.setStartNodeId(start.getId());

        Map<String, List<SpfDefinition.SpfEdge>> outEdges = CollectionUtils.isEmpty(def.getEdges())
                ? Map.of()
                : def.getEdges().stream().collect(Collectors.groupingBy(SpfDefinition.SpfEdge::getSource));

        Map<String, RuntimeDefinition.RuntimeNode> nodeMap = new HashMap<>();
        for (SpfDefinition.SpfNode node : def.getNodes()) {
            nodeMap.put(node.getId(), compileNode(node, outEdges.getOrDefault(node.getId(), List.of())));
        }
        runtime.setNodeMap(nodeMap);
        return runtime;
    }

    public String compileToJson(SpfDefinition def) {
        return JSON.toJSONString(compile(def));
    }

    @SuppressWarnings("unchecked")
    private RuntimeDefinition.RuntimeNode compileNode(SpfDefinition.SpfNode node,
                                                      List<SpfDefinition.SpfEdge> outEdges) {
        RuntimeDefinition.RuntimeNode runtimeNode = new RuntimeDefinition.RuntimeNode();
        runtimeNode.setType(node.getType());
        Map<String, Object> data = node.getData() != null ? node.getData() : Map.of();

        if ("approval".equals(node.getType())) {
            runtimeNode.setName(stringVal(data.get("name")));
            runtimeNode.setAssigneeRule((Map<String, Object>) data.get("assigneeRule"));
            runtimeNode.setApproveMode(stringVal(data.getOrDefault("approveMode", "OR")));
            runtimeNode.setNext(outEdges.stream().map(SpfDefinition.SpfEdge::getTarget).toList());
        } else if ("condition".equals(node.getType())) {
            runtimeNode.setName(stringVal(data.get("name")));
            List<RuntimeDefinition.RuntimeBranch> branches = new ArrayList<>();
            for (SpfDefinition.SpfEdge edge : outEdges) {
                RuntimeDefinition.RuntimeBranch branch = new RuntimeDefinition.RuntimeBranch();
                if (edge.getData() != null && edge.getData().get("condition") != null) {
                    branch.setCondition((Map<String, Object>) edge.getData().get("condition"));
                } else {
                    branch.setCondition(Map.of("type", "default"));
                }
                branch.setNext(edge.getTarget());
                branches.add(branch);
            }
            runtimeNode.setBranches(branches);
        } else if ("start".equals(node.getType())) {
            runtimeNode.setNext(outEdges.stream().map(SpfDefinition.SpfEdge::getTarget).toList());
        } else if ("end".equals(node.getType())) {
            runtimeNode.setNext(List.of());
        }
        return runtimeNode;
    }

    private String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
