package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.pivot.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体类
 *
 * @author stardust
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class DictType extends BaseEntity {

    /**
     * 字典主键
     */
    @TableId(type = IdType.AUTO)
    private Long dictId;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}

