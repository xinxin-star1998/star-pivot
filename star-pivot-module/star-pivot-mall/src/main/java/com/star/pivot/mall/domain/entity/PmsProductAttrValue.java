package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * spu属性值实体类 pms_product_attr_value
 * 
 * @author admin
 * @since 2026-05-19
 */
@Data
@TableName("pms_product_attr_value" )
public class PmsProductAttrValue implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long spuId;

    /**
     * 属性id
     */
    private Long attrId;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 属性值
     */
    private String attrValue;

    /**
     * 顺序
     */
    private Long attrSort;

    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】
     */
    private Long quickShow;

}