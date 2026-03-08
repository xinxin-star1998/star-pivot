package com.star.pivot.system.service;

import com.star.pivot.framework.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导入导出服务工厂类
 * 根据业务类型获取对应的导入导出服务实现
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Component
public class ImportExportServiceFactory {

    /**
     * 服务缓存，key 为业务类型，value 为对应的服务实现
     */
    private final Map<String, ImportExportService> serviceMap = new ConcurrentHashMap<>();

    /**
     * 构造函数，自动注入所有 ImportExportService 实现
     *
     * @param services 所有 ImportExportService 实现类
     */
    @Autowired
    public ImportExportServiceFactory(List<ImportExportService> services) {
        // 将所有服务实现注册到缓存中
        for (ImportExportService service : services) {
            String businessType = service.getBusinessType();
            if (businessType == null || businessType.trim().isEmpty()) {
                throw new IllegalStateException("业务类型不能为空: " + service.getClass().getName());
            }
            serviceMap.put(businessType.toLowerCase(), service);
        }
    }

    /**
     * 根据业务类型获取对应的导入导出服务
     *
     * @param businessType 业务类型标识（如：user、dept、role 等）
     * @return 对应的导入导出服务实现
     * @throws BusinessException 如果业务类型不存在
     */
    public ImportExportService getService(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            throw new BizException("业务类型不能为空");
        }

        ImportExportService service = serviceMap.get(businessType.toLowerCase());
        if (service == null) {
            throw new BizException("未找到业务类型为 [" + businessType + "] 的导入导出服务");
        }

        return service;
    }

    /**
     * 检查业务类型是否存在
     *
     * @param businessType 业务类型标识
     * @return 是否存在
     */
    public boolean hasService(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return false;
        }
        return serviceMap.containsKey(businessType.toLowerCase());
    }
}
