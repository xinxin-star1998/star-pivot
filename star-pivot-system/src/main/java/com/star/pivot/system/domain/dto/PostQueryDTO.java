package com.star.pivot.system.domain.dto;

import lombok.Data;

/**
 * 岗位查询DTO
 *
 * @author stardust
 * @date 2024-01-01
 */
@Data
public class PostQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}

