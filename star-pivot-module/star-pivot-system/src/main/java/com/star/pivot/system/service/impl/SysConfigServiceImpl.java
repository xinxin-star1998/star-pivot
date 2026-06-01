package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.cache.CacheNames;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.constants.SysConfigKeys;
import com.star.pivot.system.domain.bo.SysConfigVO;
import com.star.pivot.system.domain.dto.SysConfigDTO;
import com.star.pivot.system.domain.dto.SysConfigQueryDTO;
import com.star.pivot.system.domain.entity.SysConfig;
import com.star.pivot.system.mapper.SysConfigMapper;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import com.star.pivot.system.utils.RedisCache;
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
        return parseBooleanConfig(selectConfigValueByKey(SysConfigKeys.REGISTER_USER));
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
        return "true".equalsIgnoreCase(StringUtils.trimWhitespace(value));
    }
}
