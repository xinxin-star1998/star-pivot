package com.star.pivot.system.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * Redis 监控信息 VO
 *
 * @author xinxin
 * @since 2026-01-25
 */
@Data
public class RedisMonitorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否可用：true 表示 Redis 已配置且连接正常；false 表示 Redis 未配置或连接失败
     */
    private Boolean available;

    /**
     * 不可用时的提示文案（如：Redis 未配置或未启用）
     */
    private String message;

    /**
     * Redis 版本
     */
    private String version;

    /**
     * Redis 运行模式
     */
    private String mode;

    /**
     * Redis 端口
     */
    private Integer port;

    /**
     * 连接状态
     */
    private Boolean connected;

    /**
     * 内存信息
     */
    private MemoryInfo memory;

    /**
     * 键值统计
     */
    private KeyInfo keys;

    /**
     * 命令统计
     */
    private CommandInfo commands;

    /**
     * 客户端信息
     */
    private ClientInfo clients;

    /**
     * 内存信息
     */
    @Data
    public static class MemoryInfo implements Serializable {
        /**
         * 已使用内存（MB）
         */
        private Long usedMemory;

        /**
         * 最大内存（MB）
         */
        private Long maxMemory;

        /**
         * 内存使用率
         */
        private Double usage;
    }

    /**
     * 键值统计
     */
    @Data
    public static class KeyInfo implements Serializable {
        /**
         * 键总数
         */
        private Long totalKeys;

        /**
         * 过期键数
         */
        private Long expiredKeys;
    }

    /**
     * 命令统计
     */
    @Data
    public static class CommandInfo implements Serializable {
        /**
         * 总命令数
         */
        private Long totalCommands;

        /**
         * 每秒命令数
         */
        private Double commandsPerSecond;
    }

    /**
     * 客户端信息
     */
    @Data
    public static class ClientInfo implements Serializable {
        /**
         * 已连接客户端数
         */
        private Integer connectedClients;

        /**
         * 阻塞客户端数
         */
        private Integer blockedClients;
    }
}
