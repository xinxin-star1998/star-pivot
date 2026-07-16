package com.star.pivot.file.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SysFileUsageSummaryVo {

    private Long fileCount;

    private Long totalBytes;

    private Long uniqueObjects;

    private List<SysFileUsageStatVo> items;
}
