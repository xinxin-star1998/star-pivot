package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 品牌
 */
@Data
@TableName("pms_brand")
public class PmsBrand {

    @TableId(type = IdType.AUTO)
    private Long brandId;

    private String name;
    private String logo;
    private String descript;
    private Integer sort;
    private Integer showStatus;
    private String firstLetter;
}
