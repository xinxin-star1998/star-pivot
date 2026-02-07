package com.star.pivot.system.domain.bo;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;

    private String username;

    private String nickname;

    /**
     * 刷新令牌
     *
     * <p>说明：
     * <ul>
     *   <li>仅用于在访问令牌过期后无感刷新，不参与权限校验</li>
     *   <li>应存储在 HttpOnly Cookie 或受保护的存储中，避免在前端代码中随意泄露</li>
     * </ul>
     */
    private String refreshToken;
}