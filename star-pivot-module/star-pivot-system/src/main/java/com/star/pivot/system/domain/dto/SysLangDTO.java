package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 语言 DTO
 */
@Data
public class SysLangDTO {

    private Long langId;

    @NotBlank(message = "语言编码不能为空")
    @Size(max = 16, message = "语言编码长度不能超过16个字符")
    private String langCode;

    @NotBlank(message = "语言名称不能为空")
    @Size(max = 50, message = "语言名称长度不能超过50个字符")
    private String langName;

    /** 是否默认（1是 0否） */
    private String isDefault;

    /** 状态（0正常 1停用） */
    private String status;

    private Integer orderNum;
}
