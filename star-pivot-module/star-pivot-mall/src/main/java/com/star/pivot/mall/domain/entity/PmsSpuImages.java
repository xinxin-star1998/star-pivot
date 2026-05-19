package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("pms_spu_images")
public class PmsSpuImages implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long spuId;
    private String imgName; // 图片名称
    private String imgUrl; // 图片URL
    private Integer imgSort; // 图片排序
    private Integer defaultImg; // 是否默认图片
}
