package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.exception.BusinessException;
import com.star.pivot.system.domain.bo.DictTypeVO;
import com.star.pivot.system.domain.dto.DictTypeDTO;
import com.star.pivot.system.domain.dto.DictTypeQueryDTO;
import com.star.pivot.system.domain.entity.DictData;
import com.star.pivot.system.domain.entity.DictType;
import com.star.pivot.system.mapper.DictDataMapper;
import com.star.pivot.system.mapper.DictTypeMapper;
import com.star.pivot.system.service.DictTypeService;
import com.star.pivot.system.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现类
 *
 * @author stardust
 * @date 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;

    @Override
    public PageResponse<DictTypeVO> selectDictTypePage(DictTypeQueryDTO queryDTO) {
        PageResponse<DictTypeVO> pageResponse = new PageResponse<>();
        Page<DictType> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        wrapper.like(StringUtils.hasText(queryDTO.getDictName()), DictType::getDictName, queryDTO.getDictName())
                .like(StringUtils.hasText(queryDTO.getDictType()), DictType::getDictType, queryDTO.getDictType())
                .eq(StringUtils.hasText(queryDTO.getStatus()), DictType::getStatus, queryDTO.getStatus())
                .orderByDesc(DictType::getCreateTime);

        IPage<DictType> dictTypePage = this.page(page, wrapper);
        
        // 转换为VO
        IPage<DictTypeVO> voPage = new Page<>(dictTypePage.getCurrent(), dictTypePage.getSize(), dictTypePage.getTotal());
        List<DictTypeVO> voList = dictTypePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        pageResponse.setTotal(dictTypePage.getTotal());
        pageResponse.setRows(voPage.getRecords());
        pageResponse.setPageNum(voPage.getCurrent());
        pageResponse.setPageSize(voPage.getSize());
        return pageResponse;
    }

    @Override
    public DictTypeVO selectDictTypeById(Long dictId) {
        DictType dictType = this.getById(dictId);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        return convertToVO(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDictType(DictTypeDTO dictTypeDTO) {
        // 检查字典类型是否唯一
        if (!checkDictTypeUnique(dictTypeDTO.getDictType(), null)) {
            throw new BusinessException("字典类型已存在");
        }

        // 创建字典类型
        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeDTO, dictType);
        dictType.setStatus(StringUtils.hasText(dictTypeDTO.getStatus()) ? dictTypeDTO.getStatus() : "0");

        String currentUser = SecurityContextUtils.getUsername();
        dictType.setCreateBy(currentUser);
        dictType.setCreateTime(LocalDateTime.now());

        return this.save(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(DictTypeDTO dictTypeDTO) {
        DictType dictType = this.getById(dictTypeDTO.getDictId());
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        // 检查字典类型是否唯一
        if (!checkDictTypeUnique(dictTypeDTO.getDictType(), dictTypeDTO.getDictId())) {
            throw new BusinessException("字典类型已被使用");
        }

        // 如果修改了字典类型，需要更新关联的字典数据
        if (!dictType.getDictType().equals(dictTypeDTO.getDictType())) {
            LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictData::getDictType, dictType.getDictType());
            List<DictData> dictDataList = dictDataMapper.selectList(wrapper);
            for (DictData dictData : dictDataList) {
                dictData.setDictType(dictTypeDTO.getDictType());
                dictDataMapper.updateById(dictData);
            }
        }

        // 更新字典类型信息
        BeanUtils.copyProperties(dictTypeDTO, dictType, "dictId");
        String currentUser = SecurityContextUtils.getUsername();
        dictType.setUpdateBy(currentUser);
        dictType.setUpdateTime(LocalDateTime.now());

        return this.updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            DictType dictType = this.getById(dictId);
            if (dictType != null) {
                // 检查是否有字典数据
                LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DictData::getDictType, dictType.getDictType());
                long count = dictDataMapper.selectCount(wrapper);
                if (count > 0) {
                    throw new BusinessException("字典类型[" + dictType.getDictName() + "]存在字典数据，不能删除");
                }
                
                this.removeById(dictId);
            }
        }
        return true;
    }

    @Override
    public boolean checkDictTypeUnique(String dictType, Long dictId) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictType, dictType);
        if (dictId != null) {
            wrapper.ne(DictType::getDictId, dictId);
        }
        return this.count(wrapper) == 0;
    }

    /**
     * 转换为VO
     */
    private DictTypeVO convertToVO(DictType dictType) {
        DictTypeVO vo = new DictTypeVO();
        BeanUtils.copyProperties(dictType, vo);
        return vo;
    }
}

