package com.star.pivot.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 个人中心修改密码 DTO
 */
@Data
public class UpdatePasswordDTO {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5到20个字符之间")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5到20个字符之间")
    private String newPassword;
}
