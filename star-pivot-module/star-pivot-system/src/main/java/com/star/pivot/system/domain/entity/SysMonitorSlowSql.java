package com.star.pivot.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 慢SQL记录实体类
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
@TableName("sys_monitor_slow_sql")
public class SysMonitorSlowSql {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * SQL ID（Druid生成的SQL标识）
     */
    private String sqlId;

    /**
     * SQL语句
     */
    private String sqlText;

    /**
     * 执行次数
     */
    private Long executeCount;

    /**
     * 总执行时间（毫秒）
     */
    private Long executeTimeTotal;

    /**
     * 最大执行时间（毫秒）
     */
    private Long executeTimeMax;

    /**
     * 平均执行时间（毫秒）
     */
    private BigDecimal executeTimeAvg;

    /**
     * 慢SQL次数
     */
    private Long slowCount;

    /**
     * 错误次数
     */
    private Long errorCount;

    /**
     * 最后执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastExecuteTime;

    /**
     * 优化建议
     */
    private String optimizationSuggestion;

    /**
     * 状态（0待优化 1已优化 2已忽略）
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
