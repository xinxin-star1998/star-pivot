package com.star.pivot.workflow.listener;

/**
 * 流程结束回调，由业务模块实现并注册为 Spring Bean。
 */
public interface WorkflowCompletedListener {

    boolean supports(String processCode);

    void onApproved(String businessKey, Long instanceId);

    void onRejected(String businessKey, Long instanceId, String comment);
}
