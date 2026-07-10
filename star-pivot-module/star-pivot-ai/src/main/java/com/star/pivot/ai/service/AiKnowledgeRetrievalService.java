package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.vo.RagRetrievalResult;

public interface AiKnowledgeRetrievalService {

    RagRetrievalResult retrieve(String query, int topK);

    /** @deprecated 使用 {@link #retrieve(String, int)} */
    default String retrieveContext(String query, int topK) {
        return retrieve(query, topK).getContext();
    }
}
