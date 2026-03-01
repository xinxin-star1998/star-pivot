package com.star.pivot.framework.api;

import java.util.function.Consumer;

public interface OperLogApi {

    void cleanAll();

    void cleanBeforeDays(int days);

    static OperLogApi noop() {
        return new OperLogApi() {
            @Override
            public void cleanAll() {
            }

            @Override
            public void cleanBeforeDays(int days) {
            }
        };
    }
}
