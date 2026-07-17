package com.star.pivot.framework.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 国际化跨模块 API（由 system 模块实现，dict 等模块可选依赖）
 */
public interface I18nApi {

    String getDefaultLangCode();

    /**
     * 规范化语言编码（如 en-US → en），空则回退默认语言
     */
    String normalizeLang(String lang);

    /**
     * 字典标签映射 dictCode -> label（带缓存）
     */
    Map<Long, String> getDictLabelMap(String lang);

    Map<String, String> getResourceTranslations(String namespace, String resourceKey, String fieldName);

    void saveDictTranslations(Long dictCode, String defaultLabel, Map<String, String> translations);

    void deleteDictTranslations(Collection<Long> dictCodes);

    static I18nApi noop() {
        return new I18nApi() {
            @Override
            public String getDefaultLangCode() {
                return "zh";
            }

            @Override
            public String normalizeLang(String lang) {
                return lang == null || lang.isBlank() ? "zh" : lang.trim().toLowerCase();
            }

            @Override
            public Map<Long, String> getDictLabelMap(String lang) {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, String> getResourceTranslations(String namespace, String resourceKey, String fieldName) {
                return Collections.emptyMap();
            }

            @Override
            public void saveDictTranslations(Long dictCode, String defaultLabel, Map<String, String> translations) {
            }

            @Override
            public void deleteDictTranslations(Collection<Long> dictCodes) {
            }
        };
    }
}
