package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.bo.DictDataVO;
import com.star.pivot.system.domain.dto.DictDataDTO;
import com.star.pivot.system.domain.dto.DictDataQueryDTO;
import com.star.pivot.system.domain.entity.DictData;
import com.star.pivot.system.mapper.DictDataMapper;
import com.star.pivot.system.service.DictDataService;
import com.star.pivot.security.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现类
 *
 * @author stardust
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {

    private final DictDataMapper dictDataMapper;

    @Override
    public PageResponse<DictDataVO> selectDictDataPage(DictDataQueryDTO queryDTO) {
        Page<DictData> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        wrapper.like(StringUtils.hasText(queryDTO.getDictLabel()), DictData::getDictLabel, queryDTO.getDictLabel())
                .eq(StringUtils.hasText(queryDTO.getDictType()), DictData::getDictType, queryDTO.getDictType())
                .eq(StringUtils.hasText(queryDTO.getStatus()), DictData::getStatus, queryDTO.getStatus())
                .orderByAsc(DictData::getDictSort);

        IPage<DictData> dictDataPage = this.page(page, wrapper);
        
        // 转换为VO
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
    @Cacheable(cacheNames = "dictData", key = "#dictType")
    public List<DictDataVO> selectDictDataByType(String dictType) {
        List<DictData> dictDataList = dictDataMapper.selectDictDataByType(dictType);
        return dictDataList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DictDataVO selectDictDataById(Long dictCode) {
        DictData dictData = this.getById(dictCode);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        return convertToVO(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean insertDictData(DictDataDTO dictDataDTO) {
        // 创建字典数据
        DictData dictData = new DictData();
        BeanUtils.copyProperties(dictDataDTO, dictData);
        dictData.setDictSort(dictDataDTO.getDictSort() != null ? dictDataDTO.getDictSort() : 0);
        dictData.setIsDefault(StringUtils.hasText(dictDataDTO.getIsDefault()) ? dictDataDTO.getIsDefault() : "N");
        dictData.setStatus(StringUtils.hasText(dictDataDTO.getStatus()) ? dictDataDTO.getStatus() : "0");

        String currentUser = SecurityContextUtils.getUsername();
        dictData.setCreateBy(currentUser);
        dictData.setCreateTime(LocalDateTime.now());

        return this.save(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean updateDictData(DictDataDTO dictDataDTO) {
        DictData dictData = this.getById(dictDataDTO.getDictCode());
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }

        // 更新字典数据信息
        BeanUtils.copyProperties(dictDataDTO, dictData, "dictCode");
        String currentUser = SecurityContextUtils.getUsername();
        dictData.setUpdateBy(currentUser);
        dictData.setUpdateTime(LocalDateTime.now());

        return this.updateById(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "dictData", allEntries = true)
    public boolean deleteDictDataByIds(List<Long> dictCodes) {
        if (dictCodes == null || dictCodes.isEmpty()) {
            return false;
        }
        return this.removeByIds(dictCodes);
    }

    /**
     * 转换为VO
     */
    private DictDataVO convertToVO(DictData dictData) {
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(dictData, vo);
        return vo;
    }
}

