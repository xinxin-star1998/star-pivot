package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码DTO
 *
 * @author stardust
 */
@Data
public class ResetPasswordDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5到20个字符之间")
    private String password;
}

