package com.star.pivot.system.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * 申请验证码响应
 */
@Data
public class CaptchaIssueResponse {
    /**
     * 验证码类型：image / slider / drag / click
     */
    private String captchaType;

    /**
     * 服务端生成的验证码令牌（高熵、不可预测）
     */
    private String captchaToken;

    /**
     * 图形验证码 Base64 DataURL（captchaType=image）
     */
    private String captchaImage;

    /**
     * 背景图 Base64 DataURL（slider / click）
     */
    private String backgroundImage;

    /**
     * 滑块拼图块 Base64 DataURL（captchaType=slider）
     */
    private String sliderImage;

    /**
     * 拼图块纵向位置（像素，captchaType=slider）
     */
    private Integer sliderY;

    /**
     * 点选提示文案（captchaType=click），如：请依次点击【星】【枢】【管】
     */
    private String clickTip;

    /**
     * 需要点击的文字列表（顺序，captchaType=click）
     */
    private List<String> clickWords;
}
