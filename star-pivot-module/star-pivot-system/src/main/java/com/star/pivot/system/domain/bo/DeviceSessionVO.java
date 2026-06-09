package com.star.pivot.system.domain.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备会话信息VO
 */
@Data
@Schema(description = "设备会话信息")
public class DeviceSessionVO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "设备会话ID")
    private String deviceSessionId;
    
    @Schema(description = "IP地址")
    private String ipaddr;
    
    @Schema(description = "浏览器")
    private String browser;
    
    @Schema(description = "操作系统")
    private String os;
    
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @Schema(description = "最后访问时间")
    private Date lastAccessTime;
    
    @Schema(description = "会话持续时间")
    private String sessionDuration;
    
    @Schema(description = "是否为当前会话")
    @JsonProperty("isCurrent")
    private boolean current;
}
