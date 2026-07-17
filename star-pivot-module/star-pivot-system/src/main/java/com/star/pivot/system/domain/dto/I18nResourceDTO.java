package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 国际化资源批量保存 DTO
 */
@Data
public class I18nResourceDTO {

    @NotBlank(message = "命名空间不能为空")
    private String namespace;

    @NotBlank(message = "资源键不能为空")
    private String resourceKey;

    @NotBlank(message = "字段名不能为空")
    private String fieldName;

    /** lang -> content */
    @NotNull(message = "翻译内容不能为空")
    private Map<String, String> translations;
}
