package com.star.pivot.system.domain.dto;

import lombok.Data;

/**
 * 参数配置查询DTO
 *
 * @author admin
 * @since 2026-03-31
 */
@Data
public class SysConfigQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;

}
