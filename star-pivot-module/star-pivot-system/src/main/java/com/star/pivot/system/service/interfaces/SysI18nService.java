package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.dto.I18nImportDTO;
import com.star.pivot.system.domain.dto.I18nResourceDTO;
import com.star.pivot.system.domain.dto.SysLangDTO;
import com.star.pivot.system.domain.entity.SysLang;
import com.star.pivot.system.domain.vo.I18nCoverageVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 国际化服务
 */
public interface SysI18nService {

    List<SysLang> listEnabledLangs();

    List<SysLang> listAllLangs();

    boolean insertLang(SysLangDTO dto);

    boolean updateLang(SysLangDTO dto);

    boolean updateLangStatus(Long langId, String status);

    String getDefaultLangCode();

    /**
     * 解析请求语言：X-Lang → Accept-Language → 默认语言
     */
    String resolveRequestLang(HttpServletRequest request);

    /**
     * 规范化语言编码（如 en-US → en），空则回退默认语言
     */
    String normalizeLangCode(String lang);

    Map<String, String> getResourceTranslations(String namespace, String resourceKey, String fieldName);

    void saveResource(I18nResourceDTO dto);

    Map<String, String> getBundle(String namespace, String lang);

    /**
     * 对比基准语言与目标语言的翻译覆盖率
     */
    I18nCoverageVO getCoverage(String namespace, String targetLang, String fieldName);

    /**
     * 批量导入翻译，返回写入条数
     */
    int importTranslations(I18nImportDTO dto);

    /**
     * 菜单标题映射 menuId -> title（带 Redis 缓存）
     */
    Map<Long, String> getMenuTitleMap(String lang);

    /**
     * 字典标签映射 dictCode -> label（带 Redis 缓存）
     */
    Map<Long, String> getDictLabelMap(String lang);

    /**
     * 保存菜单多语言：默认语言同步 menu_name，其余写入 translations
     */
    void saveMenuTranslations(Long menuId, String defaultName, Map<String, String> translations);

    /**
     * 保存字典多语言：默认语言同步 dict_label，其余写入 translations
     */
    void saveDictTranslations(Long dictCode, String defaultLabel, Map<String, String> translations);

    void deleteMenuTranslations(Collection<Long> menuIds);

    void deleteDictTranslations(Collection<Long> dictCodes);

    void clearMenuI18nCache();

    void clearDictI18nCache();

    void clearUiI18nCache();
}
