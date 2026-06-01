package com.star.pivot.mall.support;

/**
 * 商品图片上传常量
 */
public final class GoodsImageConstants {

    public static final String CATEGORY = "goods";

    public static final String[] ALLOWED_CONTENT_TYPES = {
            "image/png", "image/jpeg", "image/gif", "image/webp"
    };

    /** 单张最大 5MB */
    public static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;

    private GoodsImageConstants() {
    }
}
