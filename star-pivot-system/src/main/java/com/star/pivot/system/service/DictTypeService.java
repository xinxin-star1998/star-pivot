package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.system.domain.bo.DictTypeVO;
import com.star.pivot.system.domain.dto.DictTypeDTO;
import com.star.pivot.system.domain.dto.DictTypeQueryDTO;
import com.star.pivot.system.domain.entity.DictType;

/**
 * 字典类型服务接口
 *
 * @author stardust
 * @since 2024-01-01
 */
public interface DictTypeService extends IService<DictType> {

    /**
     * 分页查询字典类型列表
     *
     * @param queryDTO 查询条件
     * @return 字典类型分页列表
     */
    PageResponse<DictTypeVO> selectDictTypePage(DictTypeQueryDTO queryDTO);

    /**
     * 根据字典类型ID查询详情
     *
     * @param dictId 字典类型ID
     * @return 字典类型详情
     */
    DictTypeVO selectDictTypeById(Long dictId);

    /**
     * 新增字典类型
     *
     * @param dictTypeDTO 字典类型信息
     * @return 是否成功
     */
    boolean insertDictType(DictTypeDTO dictTypeDTO);

    /**
     * 修改字典类型
     *
     * @param dictTypeDTO 字典类型信息
     * @return 是否成功
     */
    boolean updateDictType(DictTypeDTO dictTypeDTO);

    /**
     * 删除字典类型
     *
     * @param dictIds 字典类型ID数组
     * @return 是否成功
     */
    boolean deleteDictTypeByIds(Long[] dictIds);

    /**
     * 检查字典类型是否唯一
     *
     * @param dictType 字典类型
     * @param dictId 字典类型ID（修改时传入）
     * @return 是否唯一
     */
    boolean checkDictTypeUnique(String dictType, Long dictId);
}

