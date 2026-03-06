package com.star.pivot.system.service;

import com.star.pivot.dict.service.DictDataService;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.service.impl.SysMenuServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存预热服务
 * 
 * <p>应用启动时预热常用缓存，提升系统启动后的首次访问性能
 * 
 * <p>预热内容：
 * <ul>
 *   <li>菜单树缓存：系统启动时预加载，避免首次访问菜单时查询数据库</li>
 *   <li>常用字典数据：预加载常用字典类型，如用户性别、显示隐藏等</li>
 * </ul>
 * 
 * <p>注意事项：
 * <ul>
 *   <li>预热操作在应用启动完成后执行（@PostConstruct）</li>
 *   <li>预热失败不影响应用启动，只记录警告日志</li>
 *   <li>预热操作是异步的，不阻塞应用启动流程</li>
 * </ul>
 * 
 * @author xinxin
 * @since 2026-01-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmupService {

    private final SysMenuServiceImpl sysMenuService;
    private final DictDataService dictDataService;

    /**
     * 应用启动时预热缓存
     */
    @PostConstruct
    public void warmupCache() {
        log.info("开始预热缓存...");
        
        try {
            // 预热菜单树缓存
            warmupMenuTreeCache();
            
            // 预热常用字典数据
            warmupDictDataCache();
            
            log.info("缓存预热完成");
        } catch (Exception e) {
            log.warn("缓存预热失败，不影响应用启动", e);
        }
    }

    /**
     * 预热菜单树缓存
     */
    private void warmupMenuTreeCache() {
        try {
            log.debug("预热菜单树缓存...");
            List<SysMenu> menuTree = sysMenuService.menuTree();
            log.info("菜单树缓存预热成功，菜单数量: {}", menuTree != null ? menuTree.size() : 0);
        } catch (Exception e) {
            log.warn("预热菜单树缓存失败", e);
        }
    }

    /**
     * 预热常用字典数据缓存
     * 
     * <p>预加载常用字典类型，如：
     * <ul>
     *   <li>sys_user_sex: 用户性别</li>
     *   <li>sys_show_hide: 显示隐藏</li>
     *   <li>sys_normal_disable: 正常停用</li>
     * </ul>
     */
    private void warmupDictDataCache() {
        try {
            log.debug("预热字典数据缓存...");
            
            // 常用字典类型列表
            String[] commonDictTypes = {
                "sys_user_sex",
                "sys_show_hide",
                "sys_normal_disable"
            };
            
            int successCount = 0;
            for (String dictType : commonDictTypes) {
                try {
                    dictDataService.selectDictDataByType(dictType);
                    successCount++;
                    log.debug("字典类型 {} 预热成功", dictType);
                } catch (Exception e) {
                    log.warn("字典类型 {} 预热失败", dictType, e);
                }
            }
            
            log.info("字典数据缓存预热完成，成功: {}/{}", successCount, commonDictTypes.length);
        } catch (Exception e) {
            log.warn("预热字典数据缓存失败", e);
        }
    }
}
