package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * spu信息介绍实体类 pms_spu_info_desc
 * 
 * @author admin
 * @since 2026-05-19
 */
@Data
@TableName("pms_spu_info_desc" )
public class PmsSpuInfoDesc implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @TableId(value = "spu_id" , type = IdType.AUTO)
    private Long spuId;

    /**
     * 商品介绍
     */
    private String decript;

}