package com.star.pivot.common.utils;

import java.util.List;
import java.util.function.Consumer;

/**
 * 服务层通用工具类
 *
 * <p>抽取常用工具方法，用于批量处理、分批更新等场景，避免一次性处理过多数据导致性能问题。
 *
 * @author xinxin
 * @since 2026-01-25
 */
public final class ServiceUtils {

    private ServiceUtils() {
    }

    /**
     * 批量处理：将列表按指定大小分批，每批执行一次处理逻辑
     *
     * <p>使用场景：批量插入、批量更新、批量删除等，避免单次操作数据量过大导致超时或锁表。
     *
     * @param list       待处理列表（不可为 null）
     * @param batchSize  每批大小（建议 100～500，视业务而定）
     * @param processFunc 每批数据的处理逻辑（如 mapper.insertBatch、批量更新等）
     * @param <T>        元素类型
     */
    public static <T> void batchProcess(List<T> list, int batchSize, Consumer<List<T>> processFunc) {
        if (list == null || list.isEmpty() || processFunc == null || batchSize <= 0) {
            return;
        }
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            List<T> batch = list.subList(i, end);
            processFunc.accept(batch);
        }
    }

    /**
     * 批量更新：将列表按指定大小分批，每批执行一次更新逻辑
     *
     * <p>等价于 {@link #batchProcess}，命名上更明确表达“批量更新”的用途。
     *
     * @param list       待更新列表（不可为 null）
     * @param batchSize  每批大小
     * @param updateFunc 每批数据的更新逻辑
     * @param <T>        元素类型
     */
    public static <T> void batchUpdate(List<T> list, int batchSize, Consumer<List<T>> updateFunc) {
        batchProcess(list, batchSize, updateFunc);
    }
}
