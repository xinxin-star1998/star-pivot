package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.pivot.framework.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 参数配置实体类 sys_config
 *
 * @author admin
 * @since 2026-03-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @TableId(value = "config_id", type = IdType.AUTO)
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

}
