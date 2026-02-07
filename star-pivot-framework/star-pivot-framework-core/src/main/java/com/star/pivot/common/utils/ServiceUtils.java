package com.star.pivot.common.utils;

import java.util.List;
import java.util.function.Consumer;

public final class ServiceUtils {

    private ServiceUtils() {}

    public static <T> void batchProcess(List<T> list, int batchSize, Consumer<List<T>> processFunc) {
        if (list == null || list.isEmpty() || processFunc == null || batchSize <= 0) return;
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            processFunc.accept(list.subList(i, end));
        }
    }

    public static <T> void batchUpdate(List<T> list, int batchSize, Consumer<List<T>> updateFunc) {
        batchProcess(list, batchSize, updateFunc);
    }
}
