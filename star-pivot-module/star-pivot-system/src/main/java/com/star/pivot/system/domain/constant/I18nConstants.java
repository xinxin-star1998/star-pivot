package com.star.pivot.system.domain.constant;

/**
 * 国际化常量
 */
public final class I18nConstants {

    private I18nConstants() {
    }

    public static final String NAMESPACE_MENU = "menu";
    public static final String NAMESPACE_DICT_DATA = "dict_data";
    public static final String NAMESPACE_UI = "ui";
    public static final String FIELD_MENU_NAME = "menu_name";
    public static final String FIELD_DICT_LABEL = "dict_label";
    /** UI 文案字段占位（resource_key 即为完整 i18n key） */
    public static final String FIELD_UI = "_";
    public static final String HEADER_X_LANG = "X-Lang";
    public static final String CACHE_MENU_PREFIX = "i18n:menu:";
    public static final String CACHE_DICT_PREFIX = "i18n:dict:";
    public static final String CACHE_UI_PREFIX = "i18n:ui:";
    public static final String DEFAULT_LANG = "zh";
    public static final String IS_DEFAULT_YES = "1";
}
