package com.star.pivot.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.dict.domain.bo.DictDataVO;
import com.star.pivot.dict.domain.dto.DictDataDTO;
import com.star.pivot.dict.domain.dto.DictDataQueryDTO;
import com.star.pivot.dict.domain.entity.DictData;
import com.star.pivot.dict.mapper.DictDataMapper;
import com.star.pivot.dict.service.DictDataService;
import com.star.pivot.framework.api.I18nApi;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现类
 *
 * @author stardust
 * @since 2024-01-01
 */
@Service
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {

    private static final String I18N_NAMESPACE = "dict_data";
    private static final String I18N_FIELD = "dict_label";

    private final DictDataMapper dictDataMapper;
    private final ObjectProvider<I18nApi> i18nApiProvider;
    private final DictDataService self;

    public DictDataServiceImpl(DictDataMapper dictDataMapper,
                               ObjectProvider<I18nApi> i18nApiProvider,
                               @Lazy DictDataService self) {
        this.dictDataMapper = dictDataMapper;
        this.i18nApiProvider = i18nApiProvider;
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DictDataVO> selectDictDataPage(DictDataQueryDTO queryDTO) {
        Page<DictData> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(queryDTO.getDictLabel()), DictData::getDictLabel, queryDTO.getDictLabel())
                .eq(StringUtils.hasText(queryDTO.getDictType()), DictData::getDictType, queryDTO.getDictType())
                .eq(StringUtils.hasText(queryDTO.getStatus()), DictData::getStatus, queryDTO.getStatus())
                .orderByAsc(DictData::getDictSort);

        IPage<DictData> dictDataPage = this.page(page, wrapper);

        List<DictDataVO> voList = dictDataPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();
        PageResponse<DictDataVO> pageResponse = new PageResponse<>();
        pageResponse.setTotal(dictDataPage.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(Long.valueOf(queryDTO.getPageNum()));
        pageResponse.setPageSize(Long.valueOf(queryDTO.getPageSize()));
        return pageResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DictDataVO> selectDictDataByType(String dictType) {
        return selectDictDataByType(dictType, resolveRequestLang());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DictDataVO> selectDictDataByType(String dictType, String lang) {
        List<DictDataVO> rawList = self.loadRawDictDataByType(dictType);
        Map<Long, String> labelMap = getI18nApi().map(api -> api.getDictLabelMap(lang)).orElse(Map.of());
        if (labelMap.isEmpty()) {
            return rawList;
        }
        return rawList.stream().map(item -> {
            DictDataVO copy = new DictDataVO();
            BeanUtils.copyProperties(item, copy);
            String translated = labelMap.get(item.getDictCode());
            if (StringUtils.hasText(translated)) {
                copy.setDictLabel(translated);
            }
            return copy;
        }).collect(Collectors.toList());
    }

    /**
     * 缓存默认语言原文，避免语言切换污染缓存
     */
    @Override
    @Cacheable(cacheNames = "dictData", key = "#dictType")
    @Transactional(readOnly = true)
    public List<DictDataVO> loadRawDictDataByType(String dictType) {
        List<DictData> dictDataList = dictDataMapper.selectDictDataByType(dictType);
        return dictDataList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DictDataVO selectDictDataById(Long dictCode) {
        DictData dictData = this.getById(dictCode);
        AssertUtils.notNull(dictData, ErrorCode.DICT_NOT_FOUND);
        DictDataVO vo = convertToVO(dictData);
        getI18nApi().ifPresent(api -> {
            Map<String, String> translations = api.getResourceTranslations(
                    I18N_NAMESPACE, String.valueOf(dictCode), I18N_FIELD);
            if (translations == null || translations.isEmpty()) {
                translations = new LinkedHashMap<>();
            }
            translations.putIfAbsent(api.getDefaultLangCode(), dictData.getDictLabel());
            vo.setTranslations(translations);
        });
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean insertDictData(DictDataDTO dictDataDTO) {
        DictData dictData = new DictData();
        BeanUtils.copyProperties(dictDataDTO, dictData, "translations");
        dictData.setDictSort(dictDataDTO.getDictSort() != null ? dictDataDTO.getDictSort() : 0);
        dictData.setIsDefault(StringUtils.hasText(dictDataDTO.getIsDefault()) ? dictDataDTO.getIsDefault() : "N");
        dictData.setStatus(StringUtils.hasText(dictDataDTO.getStatus()) ? dictDataDTO.getStatus() : "0");

        String currentUser = SecurityContextUtils.getUsername();
        dictData.setCreateBy(currentUser);
        dictData.setCreateTime(LocalDateTime.now());

        boolean saved = this.save(dictData);
        if (saved) {
            getI18nApi().ifPresent(api ->
                    api.saveDictTranslations(dictData.getDictCode(), dictData.getDictLabel(), dictDataDTO.getTranslations()));
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean updateDictData(DictDataDTO dictDataDTO) {
        DictData dictData = this.getById(dictDataDTO.getDictCode());
        AssertUtils.notNull(dictData, ErrorCode.DICT_NOT_FOUND);

        BeanUtils.copyProperties(dictDataDTO, dictData, "dictCode", "translations");
        String currentUser = SecurityContextUtils.getUsername();
        dictData.setUpdateBy(currentUser);
        dictData.setUpdateTime(LocalDateTime.now());

        boolean updated = this.updateById(dictData);
        if (updated) {
            getI18nApi().ifPresent(api ->
                    api.saveDictTranslations(dictData.getDictCode(), dictData.getDictLabel(), dictDataDTO.getTranslations()));
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean deleteDictDataByIds(List<Long> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return false;
        }
        boolean removed = this.removeByIds(dictCodes);
        if (removed) {
            getI18nApi().ifPresent(api -> api.deleteDictTranslations(dictCodes));
        }
        return removed;
    }

    private Optional<I18nApi> getI18nApi() {
        return Optional.ofNullable(i18nApiProvider.getIfAvailable());
    }

    private String resolveRequestLang() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        String preferred = null;
        if (request != null) {
            preferred = request.getHeader("X-Lang");
            if (!StringUtils.hasText(preferred)) {
                String accept = request.getHeader("Accept-Language");
                if (StringUtils.hasText(accept)) {
                    preferred = accept.split(",")[0].trim();
                }
            }
        }
        String finalPreferred = preferred;
        return getI18nApi()
                .map(api -> api.normalizeLang(finalPreferred))
                .orElse(StringUtils.hasText(finalPreferred) ? finalPreferred.trim().toLowerCase() : "zh");
    }

    private DictDataVO convertToVO(DictData dictData) {
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(dictData, vo);
        return vo;
    }
}
