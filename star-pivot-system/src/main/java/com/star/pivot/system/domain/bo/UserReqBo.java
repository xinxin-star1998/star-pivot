package com.star.pivot.system.domain.bo;

import com.star.pivot.common.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 *
 * @author xinxin
 * @since 2025-12-28 17:28:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserReqBo extends PageReqBo {
    /**
     * 用户账号
     */
    private String userName;
    
    /**
     * 用户昵称
     */
    private String nickName;
}
