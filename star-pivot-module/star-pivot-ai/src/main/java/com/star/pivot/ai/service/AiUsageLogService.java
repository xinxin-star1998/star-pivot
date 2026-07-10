package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.dto.AiUsageLogQueryDto;
import com.star.pivot.ai.domain.vo.AiUsageLogVo;
import com.star.pivot.ai.domain.vo.AiUsageSummaryVo;
import com.star.pivot.framework.domain.PageResponse;
import org.springframework.ai.chat.model.ChatResponse;

public interface AiUsageLogService {

    void recordSuccess(ChatResponse response, UsageContext context);

    void recordFailure(UsageContext context, String errorMessage);

    AiUsageSummaryVo summary(String beginTime, String endTime);

    PageResponse<AiUsageLogVo> pageList(AiUsageLogQueryDto query);

    record UsageContext(
            Long userId,
            String conversationId,
            String model,
            String requestType,
            long latencyMs,
            int userMessageLength,
            int completionLength) {}
}
