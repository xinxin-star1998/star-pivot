package com.star.pivot.system.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.star.pivot.common.utils.DateDeserializer;
import lombok.Data;

import java.util.Date;

/**
 * 角色查询DTO
 *
 * @author stardust
 * @date 2024-01-01
 */
@Data
public class RoleQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;
    /**
     * 开始时间
     */
    @JsonDeserialize(using = DateDeserializer.class)
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonDeserialize(using = DateDeserializer.class)
    private Date endTime;
}