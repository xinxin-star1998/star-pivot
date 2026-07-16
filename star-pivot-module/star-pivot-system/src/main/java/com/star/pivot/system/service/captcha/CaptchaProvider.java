package com.star.pivot.system.service.captcha;

import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;

/**
 * 验证码类型提供者（图形 / 滑块等）
 */
public interface CaptchaProvider {

    /**
     * 支持的类型码，如 image、slider
     */
    String getType();

    /**
     * 生成验证码（含写入 Redis 状态）
     */
    CaptchaIssueResponse generate(String scene);

    /**
     * 校验用户提交是否匹配当前状态
     */
    boolean matches(CaptchaVerifyRequest request, CaptchaState state);
}
