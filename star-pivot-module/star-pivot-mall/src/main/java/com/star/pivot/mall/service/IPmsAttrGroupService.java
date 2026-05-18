package com.star.pivot.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.mall.domain.dto.GroupAttrRelationSaveDTO;
import com.star.pivot.mall.domain.dto.PmsAttrGroupDTO;
import com.star.pivot.mall.domain.dto.PmsAttrGroupQueryDTO;
import com.star.pivot.mall.domain.entity.PmsAttrGroup;
import com.star.pivot.mall.domain.excel.PmsAttrGroupExcel;
import com.star.pivot.mall.domain.vo.GroupAttrRelationVO;
import com.star.pivot.mall.domain.vo.PmsAttrGroupVO;

import java.util.List;

/**
 * 属性分组Service接口
 * 
 * @author admin
 * @since 2026-05-18
 */
public interface IPmsAttrGroupService extends IService<PmsAttrGroup>
{
    /**
     * 分页查询属性分组列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResponse<PmsAttrGroupVO> selectPmsAttrGroupPage(PmsAttrGroupQueryDTO queryDTO);

    /**
     * 根据主键查询属性分组详细信息
     * 
     * @param attrGroupId 属性分组主键
     * @return 属性分组信息
     */
    PmsAttrGroupVO selectPmsAttrGroupByAttrGroupId(Long attrGroupId);

    /**
     * 新增属性分组
     * 
     * @param pmsAttrGroupDTO 属性分组信息
     * @return 是否成功
     */
    boolean insertPmsAttrGroup(PmsAttrGroupDTO pmsAttrGroupDTO);

    /**
     * 修改属性分组
     * 
     * @param pmsAttrGroupDTO 属性分组信息
     * @return 是否成功
     */
    boolean updatePmsAttrGroup(PmsAttrGroupDTO pmsAttrGroupDTO);

    /**
     * 批量删除属性分组
     * 
     * @param attrGroupIds 需要删除的属性分组主键数组
     * @return 是否成功
     */
    boolean deletePmsAttrGroupByAttrGroupIds(Long[] attrGroupIds);

    /** 查询分组所属分类下的基本属性及关联状态 */
    List<GroupAttrRelationVO> listGroupAttrRelations(Long attrGroupId);

    /** 全量保存分组关联的基本属性（先清空本分组再写入） */
    boolean saveGroupAttrRelations(Long attrGroupId, GroupAttrRelationSaveDTO saveDTO);

    /** 按查询条件导出 Excel 行 */
    List<PmsAttrGroupExcel> listForExport(PmsAttrGroupQueryDTO queryDTO);

    /**
     * 从 Excel 导入属性分组
     *
     * @param rows           解析后的行
     * @param updateSupport  为 true 且填写分组ID 时执行更新
     * @return 成功条数
     */
    int importFromExcel(List<PmsAttrGroupExcel> rows, boolean updateSupport);
}
