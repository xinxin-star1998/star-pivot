package com.star.pivot.system.domain.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位VO
 *
 * @author stardust
 * @date 2024-01-01
 */
@Data
public class PostVO {

    /**
     * 岗位ID
     */
    private Long postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 显示顺序
     */
    private Integer postSort;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;
}

