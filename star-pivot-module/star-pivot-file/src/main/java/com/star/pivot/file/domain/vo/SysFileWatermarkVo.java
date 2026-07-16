package com.star.pivot.file.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysFileWatermarkVo {

    private boolean enabled;

    /** 已解析的水印文案 */
    private String content;

    private Integer fontSize;

    private String fontColor;

    private Integer rotate;

    private Integer gapX;

    private Integer gapY;

    /** 图片下载是否打水印 */
    private boolean downloadEnabled;
}
