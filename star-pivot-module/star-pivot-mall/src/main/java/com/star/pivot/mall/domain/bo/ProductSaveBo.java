package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 商品新增/修改入参
 */
@Data
public class ProductSaveBo {

    /** 修改时必填 */
    private Long id;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 可选，未绑定品牌时为空 */
    private Long brandId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200")
    private String name;

    @Size(max = 500, message = "副标题长度不能超过500")
    private String subtitle;

    @Size(max = 512, message = "主图地址长度不能超过512")
    private String mainImage;

    private String images;

    private String detail;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "价格不能为负数")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
