package com.star.pivot.system.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * 校验验证码请求（一次性）
 */
@Data
public class CaptchaVerifyRequest {
    private String captchaToken;

    /**
     * 图形验证码用户输入（captchaType=image）
     */
    private String code;

    /**
     * 滑块/拖动条拖动后的 X 偏移（像素，captchaType=slider / drag）
     */
    private Integer sliderX;

    /**
     * 点选坐标序列（按点击顺序，captchaType=click）
     */
    private List<CaptchaClickPoint> clickPoints;

    /**
     * 业务场景（login/register/reset 等），可选
     */
    private String scene;
}
