package com.star.pivot.ai.service;

import com.star.pivot.ai.config.AiRuntimeSnapshot;

public interface AiRuntimeConfigService {

    AiRuntimeSnapshot current();

    void refresh();
}
