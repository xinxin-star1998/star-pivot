package com.star.pivot.file.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.domain.PageReqBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysFileQueryDTO extends PageReqBo {

    private Long folderId;

    private String category;

    private String mediaType;

    private String fileName;

    /**
     * 增强检索：匹配文件名 / 备注 / 标签名（优先于 fileName）
     */
    private String keyword;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /** 数据权限（服务端填充，前端勿传） */
    private DataScope dataScope;

    private List<Long> deptIds;

    private Long userId;

    private Long userDeptId;

    /**
     * 列表范围：空/all=全部，favorite=收藏，recent=最近访问
     */
    private String listScope;

    /** 按标签筛选 */
    private Long tagId;

    /** 当前登录用户（服务端填充，用于收藏/最近/标签） */
    private Long currentUserId;
}
