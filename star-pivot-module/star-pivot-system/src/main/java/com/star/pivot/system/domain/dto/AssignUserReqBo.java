package com.star.pivot.system.domain.dto;

import com.star.pivot.framework.domain.PageReqBo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分配用户   查询参数
 *
 * @author xinxin
 * @since 2025-12-28 17:28:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssignUserReqBo extends PageReqBo {
    /**
     * 用户账号
     */
    private String userName;
    private String phonenumber;
    @NotBlank(message = "角色ID不能为空")
    private String roleId;
}