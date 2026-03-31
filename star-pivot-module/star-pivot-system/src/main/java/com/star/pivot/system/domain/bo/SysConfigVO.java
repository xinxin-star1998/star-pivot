package com.star.pivot.system.domain.bo;

import lombok.Data;

/**
 * 参数配置VO
 *
 * @author admin
 * @date 2026-03-31
 */
@Data
public class SysConfigVO {

    /**
     * 参数主键
     */
    private Long configId;

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

    /**
     * 备注
     */
    private String remark;

}
