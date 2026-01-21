package com.star.pivot.generator.domain.dto;

import lombok.Data;

/**
 * 代码生成表查询DTO
 *
 * @author xinxin
 * @since 2025-01-17
 */
@Data
public class GenTableQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称
     */
    private String className;
}
