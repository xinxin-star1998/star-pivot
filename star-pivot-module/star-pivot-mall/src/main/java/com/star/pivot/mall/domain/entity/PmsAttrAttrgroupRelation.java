package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 属性与属性分组关联表 {@code pms_attr_attrgroup_relation}。
 * <p>
 * 一个属性在同一时刻通常只关联一个分组（业务上先删后插一条）；
 * 分组与三级分类的对应关系在 {@code pms_attr_group.catelog_id} 维护。
 */
@Data
@TableName("pms_attr_attrgroup_relation")
public class PmsAttrAttrgroupRelation {

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 属性 id，对应 pms_attr.attr_id */
    private Long attrId;

    /** 属性分组 id，对应 pms_attr_group.attr_group_id */
    private Long attrGroupId;

    /** 该属性在分组内的展示排序 */
    private Integer attrSort;
}
