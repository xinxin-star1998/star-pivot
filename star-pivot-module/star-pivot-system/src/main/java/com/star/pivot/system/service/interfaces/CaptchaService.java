package com.star.pivot.system.service.interfaces;

import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 申请验证码
     *
     * @param scene 业务场景（如 login/register/reset），可选
     * @return 包含 captchaToken 和 base64 图片
     */
    CaptchaIssueResponse generateCaptcha(String scene);

    /**
     * 校验验证码，一次性
     *
     * @param request 校验请求
     * @return 校验通过后返回短期 proof
     */
    CaptchaVerifyResponse verifyCaptcha(CaptchaVerifyRequest request);

    /**
     * 业务接口消费验证码 proof，一次性
     *
     * @param captchaProof 验证码通过凭证
     * @param scene        业务场景，防止跨场景复用
     * @return 是否校验通过（成功会销毁 proof）
     */
    boolean validateAndConsumeCaptchaProof(String captchaProof, String scene);
}