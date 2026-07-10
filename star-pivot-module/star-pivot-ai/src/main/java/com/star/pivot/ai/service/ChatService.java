package com.star.pivot.ai.service;

import com.star.pivot.ai.domain.dto.ChatSendDto;
import com.star.pivot.ai.domain.dto.SessionRenameDto;
import com.star.pivot.ai.domain.vo.AiHealthVo;
import com.star.pivot.ai.domain.vo.ChatHistoryMessageVo;
import com.star.pivot.ai.domain.vo.ChatReplyVo;
import com.star.pivot.ai.domain.vo.ChatSessionVo;
import com.star.pivot.ai.service.chat.ChatHealthService;
import com.star.pivot.ai.service.chat.ChatSendService;
import com.star.pivot.ai.service.chat.ChatSessionService;
import com.star.pivot.ai.service.chat.ChatStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 对话门面：对外保持原有 API，内部委托给拆分后的子服务。
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSendService chatSendService;
    private final ChatStreamService chatStreamService;
    private final ChatSessionService chatSessionService;
    private final ChatHealthService chatHealthService;

    public ChatReplyVo send(ChatSendDto dto) {
        return chatSendService.send(dto);
    }

    public SseEmitter stream(ChatSendDto dto) {
        return chatStreamService.stream(dto);
    }

    public AiHealthVo health() {
        return chatHealthService.health();
    }

    public void clearHistory(String conversationId) {
        chatSessionService.clearHistory(conversationId);
    }

    public ChatSessionVo createSession() {
        return chatSessionService.createSession();
    }

    public List<ChatSessionVo> listSessions() {
        return chatSessionService.listSessions();
    }

    public List<ChatHistoryMessageVo> listMessages(String conversationId) {
        return chatSessionService.listMessages(conversationId);
    }

    public void deleteSession(String conversationId) {
        chatSessionService.deleteSession(conversationId);
    }

    public ChatSessionVo renameSession(SessionRenameDto dto) {
        return chatSessionService.renameSession(dto);
    }
}
