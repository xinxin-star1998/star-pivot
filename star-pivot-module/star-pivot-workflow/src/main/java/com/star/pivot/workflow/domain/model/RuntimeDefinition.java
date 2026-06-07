package com.star.pivot.workflow.domain.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/** 运行态流程定义（编译后） */
@Data
public class RuntimeDefinition {

    private String schemaVersion;

    private String processCode;

    private String startNodeId;

    private Map<String, RuntimeNode> nodeMap;

    @Data
    public static class RuntimeNode {
        private String type;
        private String name;
        private Map<String, Object> assigneeRule;
        private String approveMode;
        private List<String> next;
        private List<RuntimeBranch> branches;
    }

    @Data
    public static class RuntimeBranch {
        private Map<String, Object> condition;
        private String next;
    }
}
