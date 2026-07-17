package com.star.pivot.system.service.impl;

import com.star.pivot.framework.api.I18nApi;
import com.star.pivot.system.service.interfaces.SysI18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * 国际化跨模块 API 实现
 */
@Service
@RequiredArgsConstructor
public class I18nApiImpl implements I18nApi {

    private final SysI18nService sysI18nService;

    @Override
    public String getDefaultLangCode() {
        return sysI18nService.getDefaultLangCode();
    }

    @Override
    public String normalizeLang(String lang) {
        return sysI18nService.normalizeLangCode(lang);
    }

    @Override
    public Map<Long, String> getDictLabelMap(String lang) {
        return sysI18nService.getDictLabelMap(lang);
    }

    @Override
    public Map<String, String> getResourceTranslations(String namespace, String resourceKey, String fieldName) {
        return sysI18nService.getResourceTranslations(namespace, resourceKey, fieldName);
    }

    @Override
    public void saveDictTranslations(Long dictCode, String defaultLabel, Map<String, String> translations) {
        sysI18nService.saveDictTranslations(dictCode, defaultLabel, translations);
    }

    @Override
    public void deleteDictTranslations(Collection<Long> dictCodes) {
        sysI18nService.deleteDictTranslations(dictCodes);
    }
}
