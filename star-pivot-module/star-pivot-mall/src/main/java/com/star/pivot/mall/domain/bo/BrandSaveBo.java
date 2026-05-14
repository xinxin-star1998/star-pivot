package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品牌新增/修改入参
 */
@Data
public class BrandSaveBo {

    /** 修改时必填 */
    private Long id;

    @NotBlank(message = "品牌名称不能为空")
    @Size(max = 128, message = "品牌名称长度不能超过128")
    private String brandName;

    @Size(max = 512, message = "Logo地址长度不能超过512")
    private String brandLogo;

    private String brandDesc;

    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
