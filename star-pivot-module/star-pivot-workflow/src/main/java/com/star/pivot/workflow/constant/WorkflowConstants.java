package com.star.pivot.workflow.constant;

public final class WorkflowConstants {

    private WorkflowConstants() {
    }

    public static final String DEF_STATUS_DRAFT = "draft";
    public static final String DEF_STATUS_PUBLISHED = "published";
    public static final String DEF_STATUS_DISABLED = "disabled";

    public static final String INSTANCE_RUNNING = "RUNNING";
    public static final String INSTANCE_APPROVED = "APPROVED";
    public static final String INSTANCE_REJECTED = "REJECTED";
    public static final String INSTANCE_CANCELLED = "CANCELLED";

    public static final String TASK_PENDING = "PENDING";
    public static final String TASK_COMPLETED = "COMPLETED";
    public static final String TASK_CANCELLED = "CANCELLED";

    public static final String ACTION_START = "START";
    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_CANCEL = "CANCEL";

    public static final String APPROVE_MODE_OR = "OR";
    public static final String APPROVE_MODE_AND = "AND";
}
