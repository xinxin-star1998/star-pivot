package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 申请验证码响应
 */
@Data
public class CaptchaIssueResponse {
    /**
     * 服务端生成的验证码令牌（高熵、不可预测）
     */
    private String captchaToken;

    /**
     * Base64 DataURL 图片
     */
    private String captchaImage;
}

