package com.star.pivot.mall.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 分类新增/修改（pms_category） */
@Data
public class CategorySaveBo {

    /** 修改时必填 */
    private Long catId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 128, message = "分类名称长度不能超过128")
    private String name;

    /**
     * 新增时父级类目 ID；为 null 或 0 表示一级类目。修改时忽略该字段。
     */
    private Long parentCid;

    private Integer sort;

    @NotNull(message = "显示状态不能为空")
    private Integer showStatus;

    @Size(max = 512, message = "图标长度不能超过512")
    private String icon;

    @Size(max = 32, message = "计量单位长度不能超过32")
    private String productUnit;
}
