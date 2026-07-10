package com.star.pivot.ai.service.impl;

import com.star.pivot.ai.domain.entity.AiUsageLog;
import com.star.pivot.ai.mapper.AiUsageLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiUsageLogAsyncWriter {

    private final AiUsageLogMapper aiUsageLogMapper;

    @Async("usageLogExecutor")
    public void write(AiUsageLog entity) {
        try {
            aiUsageLogMapper.insert(entity);
        } catch (Exception ex) {
            log.warn("[UsageLog] async write failed: {}", ex.getMessage());
        }
    }
}
