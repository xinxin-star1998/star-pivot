package com.star.pivot.system.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册相关公开配置（匿名可读）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterConfigResponse {

    /** 是否开放用户自助注册 */
    private boolean registerEnabled;
}
