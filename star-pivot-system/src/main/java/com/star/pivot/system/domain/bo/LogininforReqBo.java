package com.star.pivot.system.domain.bo;

import com.star.pivot.common.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录日志查询参数
 *
 * @author xinxin
 * @since 2026-01-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogininforReqBo extends PageReqBo {
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录状态（0成功 1失败）
     */
    private String status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
