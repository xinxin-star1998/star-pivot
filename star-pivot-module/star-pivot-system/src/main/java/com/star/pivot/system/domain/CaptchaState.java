package com.star.pivot.system.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码状态实体类
 * 用于在 Redis 中存储验证码的状态信息
 * 注意：不存储验证码明文，只存储哈希值和盐
 */
@Data
public class CaptchaState implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码 hash（SHA-256）
     */
    private String codeHash;

    /**
     * 随机盐
     */
    private String salt;

    /**
     * 已尝试次数
     */
    private int attempts;

    /**
     * 最大尝试次数
     */
    private int maxAttempts;

    /**
     * 业务场景（login/register/reset 等）
     */
    private String scene;

    /**
     * 验证码类型：image / slider / drag / click
     */
    private String captchaType;

    /**
     * 滑块/拖动目标 X 坐标（slider / drag；服务端私有）
     */
    private Integer targetX;

    /**
     * 点选目标坐标序列，格式 x1,y1;x2,y2;...（仅 click；服务端私有）
     */
    private String clickTargets;
}