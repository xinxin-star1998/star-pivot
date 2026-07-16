package com.star.pivot.file.support;

import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.file.domain.entity.SysFile;
import org.springframework.util.CollectionUtils;

/**
 * 文件数据权限判定。
 */
public final class FileDataScopeSupport {

    private FileDataScopeSupport() {
    }

    public static boolean isAccessible(SysFile file, DataScope scope) {
        if (file == null) {
            return false;
        }
        if (scope == null || "1=1".equals(scope.getSqlFilter())) {
            return true;
        }
        if ("EMPTY_IN".equals(scope.getSqlFilter())) {
            return false;
        }
        if (!CollectionUtils.isEmpty(scope.getDeptIds())) {
            return file.getCreateDeptId() != null && scope.getDeptIds().contains(file.getCreateDeptId());
        }
        if (scope.getUserDeptId() != null) {
            return scope.getUserDeptId().equals(file.getCreateDeptId());
        }
        if (scope.getUserId() != null) {
            return scope.getUserId().equals(file.getCreateByUserId());
        }
        return true;
    }

    public static void applyToQuery(
            DataScope scope,
            java.util.function.Consumer<DataScope> setDataScope,
            java.util.function.Consumer<java.util.List<Long>> setDeptIds,
            java.util.function.Consumer<Long> setUserId,
            java.util.function.Consumer<Long> setUserDeptId) {
        if (scope == null) {
            return;
        }
        setDataScope.accept(scope);
        setDeptIds.accept(scope.getDeptIds());
        setUserId.accept(scope.getUserId());
        setUserDeptId.accept(scope.getUserDeptId());
    }
}
