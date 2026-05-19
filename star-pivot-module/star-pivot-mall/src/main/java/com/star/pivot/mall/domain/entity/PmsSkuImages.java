package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * sku图片实体类 pms_sku_images
 * 
 * @author admin
 * @since 2026-05-19
 */
@Data
@TableName("pms_sku_images" )
public class PmsSkuImages implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    /**
     * sku_id
     */
    private Long skuId;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 排序
     */
    private Long imgSort;

    /**
     * 默认图[0 - 不是默认图，1 - 是默认图]
     */
    private Long defaultImg;

}