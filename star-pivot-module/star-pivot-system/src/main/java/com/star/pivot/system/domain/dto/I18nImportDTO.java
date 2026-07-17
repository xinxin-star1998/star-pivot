package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 国际化批量导入 DTO
 * <p>
 * 支持两种格式：
 * <ul>
 *   <li>items：逐条 resourceKey + lang + content</li>
 *   <li>bundle：扁平 key→content，配合 lang 参数写入同一语言</li>
 * </ul>
 */
@Data
public class I18nImportDTO {

    @NotBlank(message = "命名空间不能为空")
    private String namespace;

    /** 字段名，默认按 namespace 推断 */
    private String fieldName;

    /** 是否覆盖已有译文，默认 true */
    private Boolean overwrite = true;

    /** 当使用 bundle 时的目标语言 */
    private String lang;

    /** 扁平语言包：resourceKey -> content（需配合 lang） */
    private Map<String, String> bundle;

    /** 逐条导入 */
    private List<Item> items;

    @Data
    public static class Item {
        @NotBlank
        private String resourceKey;
        @NotBlank
        private String lang;
        private String content;
    }
}
