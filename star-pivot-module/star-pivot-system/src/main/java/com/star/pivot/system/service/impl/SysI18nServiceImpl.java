package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.star.pivot.dict.domain.entity.DictData;
import com.star.pivot.dict.mapper.DictDataMapper;
import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.security.context.SecurityContextUtils;
import com.star.pivot.system.domain.constant.I18nConstants;
import com.star.pivot.system.domain.dto.I18nImportDTO;
import com.star.pivot.system.domain.dto.I18nResourceDTO;
import com.star.pivot.system.domain.dto.SysLangDTO;
import com.star.pivot.system.domain.entity.SysI18n;
import com.star.pivot.system.domain.entity.SysLang;
import com.star.pivot.system.domain.entity.SysMenu;
import com.star.pivot.system.domain.vo.I18nCoverageVO;
import com.star.pivot.system.mapper.SysI18nMapper;
import com.star.pivot.system.mapper.SysLangMapper;
import com.star.pivot.system.mapper.SysMenuMapper;
import com.star.pivot.system.service.interfaces.SysI18nService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 国际化服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysI18nServiceImpl implements SysI18nService {

    private static final long I18N_CACHE_HOURS = 1L;

    private final SysLangMapper sysLangMapper;
    private final SysI18nMapper sysI18nMapper;
    private final SysMenuMapper sysMenuMapper;
    private final DictDataMapper dictDataMapper;
    private final RedisCache redisCache;

    @Override
    public List<SysLang> listEnabledLangs() {
        return sysLangMapper.selectList(new LambdaQueryWrapper<SysLang>()
                .eq(SysLang::getStatus, AppConstants.Status.NORMAL)
                .orderByAsc(SysLang::getOrderNum)
                .orderByAsc(SysLang::getLangId));
    }

    @Override
    public List<SysLang> listAllLangs() {
        return sysLangMapper.selectList(new LambdaQueryWrapper<SysLang>()
                .orderByAsc(SysLang::getOrderNum)
                .orderByAsc(SysLang::getLangId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertLang(SysLangDTO dto) {
        Long exists = sysLangMapper.selectCount(new LambdaQueryWrapper<SysLang>()
                .eq(SysLang::getLangCode, dto.getLangCode()));
        if (exists != null && exists > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "语言编码已存在");
        }
        SysLang lang = new SysLang();
        BeanUtils.copyProperties(dto, lang);
        lang.setIsDefault(StringUtils.hasText(dto.getIsDefault()) ? dto.getIsDefault() : "0");
        lang.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : AppConstants.Status.NORMAL);
        lang.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        String username = SecurityContextUtils.getUsername();
        lang.setCreateBy(username);
        lang.setCreateTime(LocalDateTime.now());
        if (I18nConstants.IS_DEFAULT_YES.equals(lang.getIsDefault())) {
            clearDefaultFlag(null);
        }
        return sysLangMapper.insert(lang) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLang(SysLangDTO dto) {
        if (dto.getLangId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "语言ID不能为空");
        }
        SysLang existing = sysLangMapper.selectById(dto.getLangId());
        if (existing == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "语言不存在");
        }
        if (StringUtils.hasText(dto.getLangCode()) && !dto.getLangCode().equals(existing.getLangCode())) {
            Long exists = sysLangMapper.selectCount(new LambdaQueryWrapper<SysLang>()
                    .eq(SysLang::getLangCode, dto.getLangCode())
                    .ne(SysLang::getLangId, dto.getLangId()));
            if (exists != null && exists > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "语言编码已存在");
            }
        }
        SysLang lang = new SysLang();
        BeanUtils.copyProperties(dto, lang);
        lang.setUpdateBy(SecurityContextUtils.getUsername());
        lang.setUpdateTime(LocalDateTime.now());
        if (I18nConstants.IS_DEFAULT_YES.equals(lang.getIsDefault())) {
            clearDefaultFlag(dto.getLangId());
        }
        return sysLangMapper.updateById(lang) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLangStatus(Long langId, String status) {
        SysLang existing = sysLangMapper.selectById(langId);
        if (existing == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "语言不存在");
        }
        if (I18nConstants.IS_DEFAULT_YES.equals(existing.getIsDefault())
                && !AppConstants.Status.NORMAL.equals(status)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "默认语言不能停用");
        }
        SysLang update = new SysLang();
        update.setLangId(langId);
        update.setStatus(status);
        update.setUpdateBy(SecurityContextUtils.getUsername());
        update.setUpdateTime(LocalDateTime.now());
        return sysLangMapper.updateById(update) > 0;
    }

    @Override
    public String getDefaultLangCode() {
        SysLang defaultLang = sysLangMapper.selectOne(new LambdaQueryWrapper<SysLang>()
                .eq(SysLang::getIsDefault, I18nConstants.IS_DEFAULT_YES)
                .eq(SysLang::getStatus, AppConstants.Status.NORMAL)
                .last("LIMIT 1"));
        if (defaultLang != null && StringUtils.hasText(defaultLang.getLangCode())) {
            return defaultLang.getLangCode();
        }
        return I18nConstants.DEFAULT_LANG;
    }

    @Override
    public String resolveRequestLang(HttpServletRequest request) {
        if (request != null) {
            String xLang = request.getHeader(I18nConstants.HEADER_X_LANG);
            if (StringUtils.hasText(xLang)) {
                return normalizeLang(xLang);
            }
            String accept = request.getHeader("Accept-Language");
            if (StringUtils.hasText(accept)) {
                String first = accept.split(",")[0].trim();
                if (StringUtils.hasText(first)) {
                    return normalizeLang(first);
                }
            }
        }
        return getDefaultLangCode();
    }

    @Override
    public String normalizeLangCode(String lang) {
        return normalizeLang(lang);
    }

    @Override
    public Map<String, String> getResourceTranslations(String namespace, String resourceKey, String fieldName) {
        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getResourceKey, resourceKey)
                .eq(SysI18n::getFieldName, fieldName));
        Map<String, String> map = new LinkedHashMap<>();
        for (SysI18n item : list) {
            map.put(item.getLang(), item.getContent());
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveResource(I18nResourceDTO dto) {
        if (dto.getTranslations() == null || dto.getTranslations().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "翻译内容不能为空");
        }
        String defaultLang = getDefaultLangCode();
        for (Map.Entry<String, String> entry : dto.getTranslations().entrySet()) {
            if (!StringUtils.hasText(entry.getKey())) {
                continue;
            }
            String content = entry.getValue() == null ? "" : entry.getValue().trim();
            if (!StringUtils.hasText(content)) {
                deleteOne(dto.getNamespace(), dto.getResourceKey(), dto.getFieldName(), entry.getKey());
                continue;
            }
            upsertOne(dto.getNamespace(), dto.getResourceKey(), dto.getFieldName(), entry.getKey(), content);
            // 菜单默认语言同步写回 sys_menu.menu_name
            if (I18nConstants.NAMESPACE_MENU.equals(dto.getNamespace())
                    && I18nConstants.FIELD_MENU_NAME.equals(dto.getFieldName())
                    && defaultLang.equals(normalizeLang(entry.getKey()))) {
                syncMenuName(dto.getResourceKey(), content);
            }
            // 字典默认语言同步写回 sys_dict_data.dict_label
            if (I18nConstants.NAMESPACE_DICT_DATA.equals(dto.getNamespace())
                    && I18nConstants.FIELD_DICT_LABEL.equals(dto.getFieldName())
                    && defaultLang.equals(normalizeLang(entry.getKey()))) {
                syncDictLabel(dto.getResourceKey(), content);
            }
        }
        if (I18nConstants.NAMESPACE_MENU.equals(dto.getNamespace())) {
            clearMenuI18nCache();
        }
        if (I18nConstants.NAMESPACE_DICT_DATA.equals(dto.getNamespace())) {
            clearDictI18nCache();
        }
        if (I18nConstants.NAMESPACE_UI.equals(dto.getNamespace())) {
            clearUiI18nCache();
        }
    }

    @Override
    public Map<String, String> getBundle(String namespace, String lang) {
        String normalized = normalizeLang(lang);
        if (I18nConstants.NAMESPACE_UI.equals(namespace)) {
            return getUiBundleCached(normalized);
        }
        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getLang, normalized));
        Map<String, String> map = new LinkedHashMap<>();
        for (SysI18n item : list) {
            String key = item.getResourceKey();
            if (I18nConstants.FIELD_MENU_NAME.equals(item.getFieldName())
                    || I18nConstants.FIELD_DICT_LABEL.equals(item.getFieldName())
                    || I18nConstants.FIELD_UI.equals(item.getFieldName())
                    || !StringUtils.hasText(item.getFieldName())) {
                map.put(key, item.getContent());
            } else {
                map.put(key + "." + item.getFieldName(), item.getContent());
            }
        }
        return map;
    }

    @Override
    public I18nCoverageVO getCoverage(String namespace, String targetLang, String fieldName) {
        if (!StringUtils.hasText(namespace)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "命名空间不能为空");
        }
        String field = resolveFieldName(namespace, fieldName);
        String baseLang = getDefaultLangCode();
        String target = normalizeLang(targetLang);
        Map<String, String> baseMap = loadFlatBundle(namespace, baseLang, field);
        Map<String, String> targetMap = loadFlatBundle(namespace, target, field);

        I18nCoverageVO vo = new I18nCoverageVO();
        vo.setNamespace(namespace);
        vo.setBaseLang(baseLang);
        vo.setTargetLang(target);
        vo.setFieldName(field);
        vo.setTotal(baseMap.size());

        int translated = 0;
        List<I18nCoverageVO.MissingItem> missingItems = new ArrayList<>();
        for (Map.Entry<String, String> entry : baseMap.entrySet()) {
            String content = targetMap.get(entry.getKey());
            if (StringUtils.hasText(content)) {
                translated++;
            } else {
                I18nCoverageVO.MissingItem item = new I18nCoverageVO.MissingItem();
                item.setResourceKey(entry.getKey());
                item.setBaseContent(entry.getValue());
                missingItems.add(item);
            }
        }
        vo.setTranslated(translated);
        vo.setMissing(missingItems.size());
        vo.setCoverageRate(baseMap.isEmpty() ? 100.0
                : Math.round(translated * 10000.0 / baseMap.size()) / 100.0);
        vo.setMissingItems(missingItems);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importTranslations(I18nImportDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getNamespace())) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "命名空间不能为空");
        }
        String field = resolveFieldName(dto.getNamespace(), dto.getFieldName());
        boolean overwrite = dto.getOverwrite() == null || Boolean.TRUE.equals(dto.getOverwrite());
        int count = 0;

        if (dto.getBundle() != null && !dto.getBundle().isEmpty()) {
            if (!StringUtils.hasText(dto.getLang())) {
                throw new BizException(ErrorCode.PARAM_NOT_NULL, "导入 bundle 时 lang 不能为空");
            }
            String lang = normalizeLang(dto.getLang());
            for (Map.Entry<String, String> entry : dto.getBundle().entrySet()) {
                if (!StringUtils.hasText(entry.getKey())) {
                    continue;
                }
                String content = entry.getValue() == null ? "" : entry.getValue().trim();
                if (!StringUtils.hasText(content)) {
                    continue;
                }
                if (!overwrite && existsTranslation(dto.getNamespace(), entry.getKey(), field, lang)) {
                    continue;
                }
                upsertOne(dto.getNamespace(), entry.getKey(), field, lang, content);
                count++;
            }
        }

        if (dto.getItems() != null) {
            for (I18nImportDTO.Item item : dto.getItems()) {
                if (item == null || !StringUtils.hasText(item.getResourceKey()) || !StringUtils.hasText(item.getLang())) {
                    continue;
                }
                String content = item.getContent() == null ? "" : item.getContent().trim();
                if (!StringUtils.hasText(content)) {
                    continue;
                }
                String lang = normalizeLang(item.getLang());
                if (!overwrite && existsTranslation(dto.getNamespace(), item.getResourceKey(), field, lang)) {
                    continue;
                }
                upsertOne(dto.getNamespace(), item.getResourceKey(), field, lang, content);
                count++;
            }
        }

        if (count == 0) {
            throw new BizException(ErrorCode.IMPORT_ERROR, "没有可导入的翻译数据");
        }

        clearCacheByNamespace(dto.getNamespace());
        return count;
    }

    private String resolveFieldName(String namespace, String fieldName) {
        if (StringUtils.hasText(fieldName)) {
            return fieldName;
        }
        if (I18nConstants.NAMESPACE_MENU.equals(namespace)) {
            return I18nConstants.FIELD_MENU_NAME;
        }
        if (I18nConstants.NAMESPACE_DICT_DATA.equals(namespace)) {
            return I18nConstants.FIELD_DICT_LABEL;
        }
        return I18nConstants.FIELD_UI;
    }

    private Map<String, String> loadFlatBundle(String namespace, String lang, String fieldName) {
        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getFieldName, fieldName)
                .eq(SysI18n::getLang, normalizeLang(lang)));
        Map<String, String> map = new LinkedHashMap<>();
        for (SysI18n item : list) {
            if (StringUtils.hasText(item.getContent())) {
                map.put(item.getResourceKey(), item.getContent());
            }
        }
        return map;
    }

    private boolean existsTranslation(String namespace, String resourceKey, String fieldName, String lang) {
        Long count = sysI18nMapper.selectCount(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getResourceKey, resourceKey)
                .eq(SysI18n::getFieldName, fieldName)
                .eq(SysI18n::getLang, lang));
        return count != null && count > 0;
    }

    private void clearCacheByNamespace(String namespace) {
        if (I18nConstants.NAMESPACE_MENU.equals(namespace)) {
            clearMenuI18nCache();
        } else if (I18nConstants.NAMESPACE_DICT_DATA.equals(namespace)) {
            clearDictI18nCache();
        } else if (I18nConstants.NAMESPACE_UI.equals(namespace)) {
            clearUiI18nCache();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getUiBundleCached(String normalizedLang) {
        String cacheKey = I18nConstants.CACHE_UI_PREFIX + normalizedLang;
        try {
            Object cached = redisCache.getCacheObject(cacheKey);
            if (cached instanceof Map<?, ?> map) {
                Map<String, String> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
                    }
                }
                return result;
            }
        } catch (Exception e) {
            log.warn("读取 UI i18n 缓存失败: {}", e.getMessage());
        }
        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, I18nConstants.NAMESPACE_UI)
                .eq(SysI18n::getLang, normalizedLang));
        Map<String, String> result = new LinkedHashMap<>();
        for (SysI18n item : list) {
            result.put(item.getResourceKey(), item.getContent());
        }
        try {
            redisCache.setCacheObject(cacheKey, result, I18N_CACHE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("写入 UI i18n 缓存失败: {}", e.getMessage());
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, String> getMenuTitleMap(String lang) {
        String normalized = normalizeLang(lang);
        String cacheKey = I18nConstants.CACHE_MENU_PREFIX + normalized;
        try {
            Object cached = redisCache.getCacheObject(cacheKey);
            if (cached instanceof Map<?, ?> map) {
                Map<Long, String> result = new HashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (e.getKey() == null || e.getValue() == null) {
                        continue;
                    }
                    Long id = e.getKey() instanceof Number n ? n.longValue() : Long.parseLong(String.valueOf(e.getKey()));
                    result.put(id, String.valueOf(e.getValue()));
                }
                return result;
            }
        } catch (Exception e) {
            log.warn("读取菜单 i18n 缓存失败: {}", e.getMessage());
        }

        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, I18nConstants.NAMESPACE_MENU)
                .eq(SysI18n::getFieldName, I18nConstants.FIELD_MENU_NAME)
                .eq(SysI18n::getLang, normalized));
        Map<Long, String> result = new HashMap<>();
        for (SysI18n item : list) {
            try {
                result.put(Long.parseLong(item.getResourceKey()), item.getContent());
            } catch (NumberFormatException ignored) {
                // skip invalid keys
            }
        }
        try {
            // Redis 序列化友好：key 用 String
            Map<String, String> cacheMap = result.entrySet().stream()
                    .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
            redisCache.setCacheObject(cacheKey, cacheMap, I18N_CACHE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("写入菜单 i18n 缓存失败: {}", e.getMessage());
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, String> getDictLabelMap(String lang) {
        String normalized = normalizeLang(lang);
        String cacheKey = I18nConstants.CACHE_DICT_PREFIX + normalized;
        try {
            Object cached = redisCache.getCacheObject(cacheKey);
            if (cached instanceof Map<?, ?> map) {
                Map<Long, String> result = new HashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (e.getKey() == null || e.getValue() == null) {
                        continue;
                    }
                    Long id = e.getKey() instanceof Number n ? n.longValue() : Long.parseLong(String.valueOf(e.getKey()));
                    result.put(id, String.valueOf(e.getValue()));
                }
                return result;
            }
        } catch (Exception e) {
            log.warn("读取字典 i18n 缓存失败: {}", e.getMessage());
        }

        List<SysI18n> list = sysI18nMapper.selectList(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, I18nConstants.NAMESPACE_DICT_DATA)
                .eq(SysI18n::getFieldName, I18nConstants.FIELD_DICT_LABEL)
                .eq(SysI18n::getLang, normalized));
        Map<Long, String> result = new HashMap<>();
        for (SysI18n item : list) {
            try {
                result.put(Long.parseLong(item.getResourceKey()), item.getContent());
            } catch (NumberFormatException ignored) {
                // skip invalid keys
            }
        }
        try {
            Map<String, String> cacheMap = result.entrySet().stream()
                    .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
            redisCache.setCacheObject(cacheKey, cacheMap, I18N_CACHE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("写入字典 i18n 缓存失败: {}", e.getMessage());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenuTranslations(Long menuId, String defaultName, Map<String, String> translations) {
        if (menuId == null) {
            return;
        }
        String defaultLang = getDefaultLangCode();
        String resourceKey = String.valueOf(menuId);
        if (StringUtils.hasText(defaultName)) {
            upsertOne(I18nConstants.NAMESPACE_MENU, resourceKey, I18nConstants.FIELD_MENU_NAME,
                    defaultLang, defaultName.trim());
        }
        if (translations != null) {
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                if (!StringUtils.hasText(entry.getKey())) {
                    continue;
                }
                String lang = normalizeLang(entry.getKey());
                if (defaultLang.equals(lang)) {
                    // 默认语言以 menuName 为准
                    continue;
                }
                String content = entry.getValue() == null ? "" : entry.getValue().trim();
                if (!StringUtils.hasText(content)) {
                    deleteOne(I18nConstants.NAMESPACE_MENU, resourceKey, I18nConstants.FIELD_MENU_NAME, lang);
                } else {
                    upsertOne(I18nConstants.NAMESPACE_MENU, resourceKey, I18nConstants.FIELD_MENU_NAME, lang, content);
                }
            }
        }
        clearMenuI18nCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDictTranslations(Long dictCode, String defaultLabel, Map<String, String> translations) {
        if (dictCode == null) {
            return;
        }
        String defaultLang = getDefaultLangCode();
        String resourceKey = String.valueOf(dictCode);
        if (StringUtils.hasText(defaultLabel)) {
            upsertOne(I18nConstants.NAMESPACE_DICT_DATA, resourceKey, I18nConstants.FIELD_DICT_LABEL,
                    defaultLang, defaultLabel.trim());
        }
        if (translations != null) {
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                if (!StringUtils.hasText(entry.getKey())) {
                    continue;
                }
                String lang = normalizeLang(entry.getKey());
                if (defaultLang.equals(lang)) {
                    continue;
                }
                String content = entry.getValue() == null ? "" : entry.getValue().trim();
                if (!StringUtils.hasText(content)) {
                    deleteOne(I18nConstants.NAMESPACE_DICT_DATA, resourceKey, I18nConstants.FIELD_DICT_LABEL, lang);
                } else {
                    upsertOne(I18nConstants.NAMESPACE_DICT_DATA, resourceKey, I18nConstants.FIELD_DICT_LABEL, lang, content);
                }
            }
        }
        clearDictI18nCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuTranslations(Collection<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<String> keys = menuIds.stream().map(String::valueOf).collect(Collectors.toList());
        sysI18nMapper.delete(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, I18nConstants.NAMESPACE_MENU)
                .in(SysI18n::getResourceKey, keys));
        clearMenuI18nCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTranslations(Collection<Long> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return;
        }
        List<String> keys = dictCodes.stream().map(String::valueOf).collect(Collectors.toList());
        sysI18nMapper.delete(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, I18nConstants.NAMESPACE_DICT_DATA)
                .in(SysI18n::getResourceKey, keys));
        clearDictI18nCache();
    }

    @Override
    public void clearMenuI18nCache() {
        clearI18nCacheByPrefix(I18nConstants.CACHE_MENU_PREFIX, "菜单");
    }

    @Override
    public void clearDictI18nCache() {
        clearI18nCacheByPrefix(I18nConstants.CACHE_DICT_PREFIX, "字典");
    }

    @Override
    public void clearUiI18nCache() {
        clearI18nCacheByPrefix(I18nConstants.CACHE_UI_PREFIX, "UI");
    }

    private void clearI18nCacheByPrefix(String prefix, String label) {
        List<SysLang> langs = listAllLangs();
        for (SysLang lang : langs) {
            try {
                redisCache.deleteObject(prefix + lang.getLangCode());
            } catch (Exception e) {
                log.warn("清除{} i18n 缓存失败 lang={}: {}", label, lang.getLangCode(), e.getMessage());
            }
        }
        try {
            redisCache.deleteObject(prefix + I18nConstants.DEFAULT_LANG);
        } catch (Exception ignored) {
            // ignore
        }
    }

    private void clearDefaultFlag(Long excludeLangId) {
        LambdaUpdateWrapper<SysLang> wrapper = new LambdaUpdateWrapper<SysLang>()
                .set(SysLang::getIsDefault, "0")
                .eq(SysLang::getIsDefault, I18nConstants.IS_DEFAULT_YES);
        if (excludeLangId != null) {
            wrapper.ne(SysLang::getLangId, excludeLangId);
        }
        sysLangMapper.update(null, wrapper);
    }

    private void upsertOne(String namespace, String resourceKey, String fieldName, String lang, String content) {
        String normalizedLang = normalizeLang(lang);
        SysI18n existing = sysI18nMapper.selectOne(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getResourceKey, resourceKey)
                .eq(SysI18n::getFieldName, fieldName)
                .eq(SysI18n::getLang, normalizedLang)
                .last("LIMIT 1"));
        String username = SecurityContextUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            SysI18n entity = new SysI18n();
            entity.setNamespace(namespace);
            entity.setResourceKey(resourceKey);
            entity.setFieldName(fieldName);
            entity.setLang(normalizedLang);
            entity.setContent(content);
            entity.setCreateBy(username);
            entity.setCreateTime(now);
            sysI18nMapper.insert(entity);
        } else {
            existing.setContent(content);
            existing.setUpdateBy(username);
            existing.setUpdateTime(now);
            sysI18nMapper.updateById(existing);
        }
    }

    private void deleteOne(String namespace, String resourceKey, String fieldName, String lang) {
        sysI18nMapper.delete(new LambdaQueryWrapper<SysI18n>()
                .eq(SysI18n::getNamespace, namespace)
                .eq(SysI18n::getResourceKey, resourceKey)
                .eq(SysI18n::getFieldName, fieldName)
                .eq(SysI18n::getLang, normalizeLang(lang)));
    }

    private void syncMenuName(String resourceKey, String content) {
        try {
            Long menuId = Long.parseLong(resourceKey);
            SysMenu menu = new SysMenu();
            menu.setMenuId(menuId);
            menu.setMenuName(content);
            menu.setUpdateBy(SecurityContextUtils.getUsername());
            menu.setUpdateTime(LocalDateTime.now());
            sysMenuMapper.updateById(menu);
        } catch (NumberFormatException e) {
            log.warn("同步菜单名称失败，非法 resourceKey={}", resourceKey);
        }
    }

    private void syncDictLabel(String resourceKey, String content) {
        try {
            Long dictCode = Long.parseLong(resourceKey);
            DictData dictData = new DictData();
            dictData.setDictCode(dictCode);
            dictData.setDictLabel(content);
            dictData.setUpdateBy(SecurityContextUtils.getUsername());
            dictData.setUpdateTime(LocalDateTime.now());
            dictDataMapper.updateById(dictData);
        } catch (NumberFormatException e) {
            log.warn("同步字典标签失败，非法 resourceKey={}", resourceKey);
        }
    }

    private String normalizeLang(String lang) {
        if (!StringUtils.hasText(lang)) {
            return getDefaultLangCode();
        }
        String value = lang.trim().replace('_', '-');
        // en-US -> en, zh-CN -> zh
        int idx = value.indexOf('-');
        if (idx > 0) {
            value = value.substring(0, idx);
        }
        return value.toLowerCase(Locale.ROOT);
    }
}
