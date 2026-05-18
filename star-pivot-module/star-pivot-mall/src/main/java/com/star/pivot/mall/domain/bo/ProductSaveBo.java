package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/**
 * SPU 新增/修改（pms_spu_info）
 */
@Data
public class ProductSaveBo {

    /** 修改时必填 */
    private Long id;

    @NotBlank(message = "SPU 名称不能为空")
    @Size(max = 200, message = "SPU 名称长度不能超过200")
    private String spuName;

    private String spuDescription;

    @NotNull(message = "分类目录不能为空")
    private Long catalogId;

    private Long brandId;

    @NotNull(message = "重量不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "重量不能为负数")
    private BigDecimal weight;

    @NotNull(message = "上架状态不能为空")
    private Integer publishStatus;
}
