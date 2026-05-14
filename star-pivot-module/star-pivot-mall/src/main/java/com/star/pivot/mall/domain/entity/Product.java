package com.star.pivot.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商品
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private Long brandId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String images;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
