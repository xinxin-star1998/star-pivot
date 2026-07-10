package com.star.pivot.ai.service.chat;

import lombok.Builder;

@Builder
public record ChatExecutionPlan(
        ChatIntent intent,
        String promptScene,
        String model,
        boolean useRag,
        boolean autoScene,
        boolean autoModel) {}
