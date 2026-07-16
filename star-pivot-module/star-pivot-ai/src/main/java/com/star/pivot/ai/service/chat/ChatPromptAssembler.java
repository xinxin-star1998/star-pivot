package com.star.pivot.ai.service.chat;

import com.star.pivot.ai.config.AiPromptTemplateSnapshot;
import com.star.pivot.ai.config.AiRuntimeSnapshot;
import com.star.pivot.ai.domain.dto.ChatSendDto;
import com.star.pivot.ai.domain.vo.RagRetrievalResult;
import com.star.pivot.ai.service.AiKnowledgeRetrievalService;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatPromptAssembler {

    private final ChatClient chatClient;
    private final MessageWindowChatMemory chatMemory;
    private final AiKnowledgeRetrievalService aiKnowledgeRetrievalService;
    private final SystemPromptResolver systemPromptResolver;

    public ChatClient.ChatClientRequestSpec buildPrompt(
            ChatSendDto dto,
            String conversationId,
            AiRuntimeSnapshot runtime,
            RagRetrievalResult ragResult,
            ChatExecutionPlan plan) {
        systemPromptResolver.assertSceneAllowed(runtime, plan.promptScene());

        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();
        if (StringUtils.hasText(plan.model())) {
            optionsBuilder.model(plan.model());
        }
        Double temperature = resolveTemperature(dto.getTemperature(), plan.promptScene(), runtime);
        if (temperature != null) {
            optionsBuilder.temperature(temperature);
        }

        return chatClient
                .prompt()
                .options(optionsBuilder.build())
                .system(buildSystemPrompt(runtime, plan.promptScene(), ragResult))
                .user(dto.getMessage())
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId)
                        .build());
    }

    public String resolveModel(String requestedModel, AiRuntimeSnapshot runtime, String promptScene) {
        AiPromptTemplateSnapshot scene = systemPromptResolver.findTemplate(runtime, promptScene);
        return resolveModel(requestedModel, scene, runtime);
    }

    public RagRetrievalResult retrieve(AiRuntimeSnapshot runtime, String userMessage, boolean useRag) {
        if (!useRag || !runtime.isRagEnabled() || !StringUtils.hasText(userMessage)) {
            return RagRetrievalResult.builder().context("").sources(java.util.List.of()).build();
        }
        // 仅具备知识库查询权限的用户可检索全局知识库，避免任意对话用户读出知识内容
        if (!SecurityContextUtils.hasAuthority("ai:knowledge:query")) {
            log.debug("Skip RAG: current user lacks ai:knowledge:query");
            return RagRetrievalResult.builder().context("").sources(java.util.List.of()).build();
        }
        try {
            return aiKnowledgeRetrievalService.retrieve(userMessage, runtime.getRagTopK());
        } catch (RuntimeException ex) {
            log.warn("RAG retrieval failed, continue without knowledge context: {}", ex.getMessage());
            return RagRetrievalResult.builder().context("").sources(java.util.List.of()).degraded(true).build();
        }
    }

    public String resolveModel(String requestedModel, AiRuntimeSnapshot runtime) {
        return resolveModel(requestedModel, null, runtime);
    }

    private String resolveModel(String requestedModel, AiPromptTemplateSnapshot scene, AiRuntimeSnapshot runtime) {
        if (StringUtils.hasText(requestedModel) && !ChatQueryRouter.AUTO.equalsIgnoreCase(requestedModel.trim())) {
            String model = requestedModel.trim();
            if (!runtime.isModelAllowed(model)) {
                throw new BizException("不支持的模型：" + model);
            }
            return model;
        }
        if (scene != null && StringUtils.hasText(scene.getModel()) && runtime.isModelAllowed(scene.getModel())) {
            return scene.getModel();
        }
        return runtime.getDefaultModel();
    }

    private Double resolveTemperature(Double requestedTemperature, String promptScene, AiRuntimeSnapshot runtime) {
        if (requestedTemperature != null) {
            return requestedTemperature;
        }
        AiPromptTemplateSnapshot scene = systemPromptResolver.findTemplate(runtime, promptScene);
        if (scene != null && scene.getTemperature() != null) {
            return scene.getTemperature();
        }
        return runtime.getDefaultTemperature();
    }

    private String buildSystemPrompt(AiRuntimeSnapshot runtime, String promptScene, RagRetrievalResult ragResult) {
        String scene = systemPromptResolver.normalizeSceneId(runtime, promptScene);
        StringBuilder builder = new StringBuilder(systemPromptResolver.resolve(runtime, promptScene).trim());
        builder.append("\n\n").append(ResponseFormatGuide.forScene(scene));
        if (StringUtils.hasText(ragResult.getContext())) {
            builder.append("\n\n").append(ResponseFormatGuide.ragInstruction());
            builder.append("\n\n").append(ragResult.getContext());
        }
        return builder.toString();
    }
}
