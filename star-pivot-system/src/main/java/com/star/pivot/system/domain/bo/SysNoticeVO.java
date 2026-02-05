package com.star.pivot.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知公告VO
 * 
 * @author admin
 * @date 2026-02-05
 */
@Data
public class SysNoticeVO {

    /**
     * 公告ID
     */
    private Integer noticeId;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;
}
