package com.star.pivot.framework.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public class OperLogCleanEvent extends ApplicationEvent {

    private final CleanType cleanType;
    private final Integer days;
    private final LocalDateTime triggerTime;

    public OperLogCleanEvent(Object source, CleanType cleanType, Integer days) {
        super(source);
        this.cleanType = cleanType;
        this.days = days;
        this.triggerTime = LocalDateTime.now();
    }

    public static OperLogCleanEvent cleanAll(Object source) {
        return new OperLogCleanEvent(source, CleanType.ALL, null);
    }

    public static OperLogCleanEvent cleanBeforeDays(Object source, int days) {
        return new OperLogCleanEvent(source, CleanType.BEFORE_DAYS, days);
    }

    public CleanType getCleanType() {
        return cleanType;
    }

    public Integer getDays() {
        return days;
    }

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public enum CleanType {
        ALL,
        BEFORE_DAYS
    }
}
