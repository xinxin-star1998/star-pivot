package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.cache.CacheNames;
import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.constants.SysConfigKeys;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigDTO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.entity.SysConfig;
import com.star.pivot.system.mapper.SysConfigMapper;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 参数配置Service业务层实现
 *
 * @author admin
 * @since 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private static final long CONFIG_CACHE_HOURS = 2L;

    private final RedisCache redisCache;
    /**
     * 分页查询参数配置列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<SysConfigVO> selectSysConfigPage(SysConfigQueryDTO queryDTO) {
        PageResponse<SysConfigVO> pageResponse = new PageResponse<>();
        Page<SysConfig> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<SysConfig> sysConfigPage = baseMapper.selectPageList(page, queryDTO);

        // 转换为VO
        java.util.List<SysConfigVO> voList = sysConfigPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        pageResponse.setTotal(sysConfigPage.getTotal());
        pageResponse.setRows(voList);
        pageResponse.setPageNum(sysConfigPage.getCurrent());
        pageResponse.setPageSize(sysConfigPage.getSize());
        pageResponse.setPageCount(sysConfigPage.getPages());
        return pageResponse;
    }

    @Override
    public java.util.List<SysConfigVO> selectSysConfigList(SysConfigQueryDTO queryDTO) {
        java.util.List<SysConfig> list = baseMapper.selectListByQuery(queryDTO);
        return list.stream().map(this::convertToVO).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据主键查询参数配置详细信息
     *
     * @param configId 参数配置主键
     * @return 参数配置信息
     */
    @Override
    public SysConfigVO selectSysConfigByConfigId(Long configId) {
        SysConfig sysConfig = baseMapper.selectSysConfigByConfigId(configId);
        if (sysConfig == null) {
            throw new BizException("参数配置不存在");
        }
        return convertToVO(sysConfig);
    }

    /**
     * 新增参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertSysConfig(SysConfigDTO sysConfigDTO) {
        SysConfig sysConfig = new SysConfig();
        BeanUtils.copyProperties(sysConfigDTO, sysConfig);
        return baseMapper.insertSysConfig(sysConfig) > 0;
    }

    /**
     * 修改参数配置
     *
     * @param sysConfigDTO 参数配置信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSysConfig(SysConfigDTO sysConfigDTO) {
        SysConfig sysConfig = baseMapper.selectSysConfigByConfigId(sysConfigDTO.getConfigId());
        if (sysConfig == null) {
            throw new BizException("参数配置不存在");
        }

        String oldConfigKey = sysConfig.getConfigKey();
        BeanUtils.copyProperties(sysConfigDTO, sysConfig, "configId");
        boolean success = baseMapper.updateSysConfig(sysConfig) > 0;
        if (success) {
            evictConfigCache(sysConfig.getConfigKey());
            if (StringUtils.hasText(oldConfigKey) && !oldConfigKey.equals(sysConfig.getConfigKey())) {
                evictConfigCache(oldConfigKey);
            }
        }
        return success;
    }

    /**
     * 批量删除参数配置
     *
     * @param configIds 需要删除的参数配置主键数组
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteSysConfigByConfigIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = baseMapper.selectSysConfigByConfigId(configId);
            if (config != null) {
                evictConfigCache(config.getConfigKey());
            }
        }
        return baseMapper.deleteSysConfigByConfigIds(configIds) > 0;
    }

    @Override
    public String selectConfigValueByKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }
        String cacheKey = buildConfigCacheKey(configKey);
        String cached = redisCache.getCacheObject(cacheKey);
        if (cached != null) {
            return cached;
        }
        SysConfig config = baseMapper.selectByConfigKey(configKey);
        if (config == null || config.getConfigValue() == null) {
            return null;
        }
        redisCache.setCacheObject(cacheKey, config.getConfigValue(), CONFIG_CACHE_HOURS, TimeUnit.HOURS);
        return config.getConfigValue();
    }

    @Override
    public boolean isRegisterUserEnabled() {
        return parseBooleanConfig(selectConfigValueByKey(SysConfigKeys.REGISTER_USER), false);
    }

    @Override
    public boolean isCaptchaEnabled() {
        return parseBooleanConfig(selectConfigValueByKey(SysConfigKeys.ACCOUNT_CAPTCHA_ENABLED),
                SysConfigKeys.DEFAULT_CAPTCHA_ENABLED);
    }

    @Override
    public String getInitPassword() {
        String value = selectConfigValueByKey(SysConfigKeys.USER_INIT_PASSWORD);
        return StringUtils.hasText(value) ? value.trim() : SysConfigKeys.DEFAULT_INIT_PASSWORD;
    }

    @Override
    public String getLoginBlackIpList() {
        String value = selectConfigValueByKey(SysConfigKeys.LOGIN_BLACK_IP_LIST);
        return value != null ? value.trim() : "";
    }

    @Override
    public int getInitPasswordModifyPolicy() {
        return parseIntConfig(selectConfigValueByKey(SysConfigKeys.ACCOUNT_INIT_PASSWORD_MODIFY),
                SysConfigKeys.DEFAULT_INIT_PASSWORD_MODIFY);
    }

    @Override
    public int getPasswordValidateDays() {
        return parseIntConfig(selectConfigValueByKey(SysConfigKeys.ACCOUNT_PASSWORD_VALIDATE_DAYS),
                SysConfigKeys.DEFAULT_PASSWORD_VALIDATE_DAYS);
    }

    @Override
    public int getCaptchaLength() {
        return clampIntConfig(selectConfigValueByKey(SysConfigKeys.ACCOUNT_CAPTCHA_LENGTH),
                SysConfigKeys.DEFAULT_CAPTCHA_LENGTH,
                SysConfigKeys.MIN_CAPTCHA_LENGTH,
                SysConfigKeys.MAX_CAPTCHA_LENGTH);
    }

    @Override
    public int getCaptchaExpireSeconds() {
        return clampIntConfig(selectConfigValueByKey(SysConfigKeys.ACCOUNT_CAPTCHA_EXPIRE_SECONDS),
                SysConfigKeys.DEFAULT_CAPTCHA_EXPIRE_SECONDS,
                SysConfigKeys.MIN_CAPTCHA_EXPIRE_SECONDS,
                SysConfigKeys.MAX_CAPTCHA_EXPIRE_SECONDS);
    }

    /**
     * 转换为VO
     *
     * @param sysConfig 实体对象
     * @return VO对象
     */
    private SysConfigVO convertToVO(SysConfig sysConfig) {
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(sysConfig, vo);
        return vo;
    }

    private void evictConfigCache(String configKey) {
        if (StringUtils.hasText(configKey)) {
            redisCache.deleteObject(buildConfigCacheKey(configKey));
        }
    }

    private String buildConfigCacheKey(String configKey) {
        return CacheNames.SYS_CONFIG + ":" + configKey;
    }

    private boolean parseBooleanConfig(String value) {
        return parseBooleanConfig(value, false);
    }

    private boolean parseBooleanConfig(String value, boolean defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(StringUtils.trimWhitespace(value));
    }

    private int parseIntConfig(String value, int defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(StringUtils.trimWhitespace(value));
        } catch (NumberFormatException ex) {
            log.warn("参数配置值无法解析为整数，使用默认值: value={}, default={}", value, defaultValue);
            return defaultValue;
        }
    }

    private int clampIntConfig(String value, int defaultValue, int min, int max) {
        int parsed = parseIntConfig(value, defaultValue);
        if (parsed < min) {
            log.warn("参数配置值低于下限，使用下限: value={}, min={}", parsed, min);
            return min;
        }
        if (parsed > max) {
            log.warn("参数配置值超过上限，使用上限: value={}, max={}", parsed, max);
            return max;
        }
        return parsed;
    }
}
