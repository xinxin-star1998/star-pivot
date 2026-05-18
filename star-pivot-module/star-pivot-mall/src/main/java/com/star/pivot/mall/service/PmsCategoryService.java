package com.star.pivot.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.mall.domain.bo.CategorySaveBo;
import com.star.pivot.mall.domain.bo.CategorySortItemBo;
import com.star.pivot.mall.domain.entity.PmsCategory;
import com.star.pivot.mall.domain.vo.CategoryTreeVo;
import java.util.List;

public interface PmsCategoryService extends IService<PmsCategory> {

    /** 全表拉取后按 parent_cid 组装树，根节点 parent_cid 为 null 或 0 */
    List<CategoryTreeVo> treeList();

    /**
     * 仅查询某一父节点下的直接子分类（按 sort、cat_id 排序）。
     * parentCid 为 null 或 0 时查一级（parent_cid 为 null 或 0）。
     */
    List<CategoryTreeVo> listChildren(Long parentCid);

    CategoryTreeVo getDetail(Long catId);

    void addCategory(CategorySaveBo bo);

    void updateCategory(CategorySaveBo bo);

    void removeCategories(List<Long> ids);

    /** 更新同一父级下多个子节点的 sort（拖拽排序） */
    void updateSortBatch(List<CategorySortItemBo> items);
}
