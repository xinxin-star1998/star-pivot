package com.star.pivot.framework.datasource;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 当前线程使用的数据源名称栈，支持嵌套 {@code @DS}。
 */
public final class DataSourceContextHolder {

    private static final ThreadLocal<Deque<String>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceContextHolder() {}

    public static void push(String dataSourceKey) {
        if (dataSourceKey != null && !dataSourceKey.isEmpty()) {
            CONTEXT.get().push(dataSourceKey);
        }
    }

    public static void pop() {
        Deque<String> deque = CONTEXT.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
    }

    /**
     * 栈顶数据源键；空栈时返回 {@code null}，由路由使用默认主数据源。
     */
    public static String peek() {
        return CONTEXT.get().peek();
    }

    public static void clear() {
        CONTEXT.get().clear();
    }
}
