package com.star.pivot.workflow.domain.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/** 设计态流程定义（SPF JSON） */
@Data
public class SpfDefinition {

    private String schemaVersion;

    private String processCode;

    private String processName;

    private String bizModule;

    private List<SpfNode> nodes;

    private List<SpfEdge> edges;

    @Data
    public static class SpfNode {
        private String id;
        private String type;
        private SpfPosition position;
        private Map<String, Object> data;
    }

    @Data
    public static class SpfPosition {
        private Double x;
        private Double y;
    }

    @Data
    public static class SpfEdge {
        private String id;
        private String source;
        private String target;
        private Map<String, Object> data;
    }
}
