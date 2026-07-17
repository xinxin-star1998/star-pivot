package com.star.pivot.system.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻译覆盖率统计
 */
@Data
public class I18nCoverageVO {

    private String namespace;
    private String baseLang;
    private String targetLang;
    private String fieldName;
    /** 基准语言 key 总数 */
    private int total;
    /** 目标语言已翻译数 */
    private int translated;
    /** 缺失数 */
    private int missing;
    /** 覆盖率 0-100 */
    private double coverageRate;
    private List<MissingItem> missingItems = new ArrayList<>();

    @Data
    public static class MissingItem {
        private String resourceKey;
        private String baseContent;
    }
}
