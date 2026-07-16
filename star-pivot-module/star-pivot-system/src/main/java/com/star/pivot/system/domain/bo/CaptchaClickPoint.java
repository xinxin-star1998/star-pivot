package com.star.pivot.system.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点选验证码坐标点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaClickPoint {
    private Integer x;
    private Integer y;
}
