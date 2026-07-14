package com.star.pivot.framework.event;

import org.springframework.context.ApplicationEvent;

public class UserPasswordChangedEvent extends ApplicationEvent {

    private final Long userId;
    private final String logoutType;

    public UserPasswordChangedEvent(Object source, Long userId, String logoutType) {
        super(source);
        this.userId = userId;
        this.logoutType = logoutType;
    }

    public static UserPasswordChangedEvent create(Object source, Long userId) {
        return new UserPasswordChangedEvent(source, userId, "1");
    }

    public Long getUserId() {
        return userId;
    }

    public String getLogoutType() {
        return logoutType;
    }
}