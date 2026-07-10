package com.star.pivot.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.ai.domain.dto.AiChatSessionQueryDto;
import com.star.pivot.ai.domain.entity.AiChatSession;
import com.star.pivot.ai.domain.vo.AiChatSessionAdminVo;
import com.star.pivot.ai.domain.vo.ChatHistoryMessageVo;
import com.star.pivot.ai.mapper.AiChatSessionMapper;
import com.star.pivot.ai.memory.ChatHistoryConverter;
import com.star.pivot.ai.memory.MysqlChatMemoryRepository;
import com.star.pivot.ai.service.AiChatSessionAdminService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiChatSessionAdminServiceImpl implements AiChatSessionAdminService {

    private final AiChatSessionMapper aiChatSessionMapper;
    private final MysqlChatMemoryRepository chatMemoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AiChatSessionAdminVo> pageList(AiChatSessionQueryDto query) {
        Page<AiChatSession> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getUserId() != null, AiChatSession::getUserId, query.getUserId())
                .like(StringUtils.hasText(query.getConversationId()), AiChatSession::getConversationId, query.getConversationId())
                .like(StringUtils.hasText(query.getTitle()), AiChatSession::getTitle, query.getTitle())
                .orderByDesc(AiChatSession::getUpdateTime)
                .orderByDesc(AiChatSession::getSessionId);
        Page<AiChatSession> result = aiChatSessionMapper.selectPage(page, wrapper);
        PageResponse<AiChatSessionAdminVo> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setRows(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatHistoryMessageVo> listMessages(String conversationId) {
        if (!StringUtils.hasText(conversationId)) {
            throw new BizException("会话 ID 不能为空");
        }
        return chatMemoryRepository.listRawMessages(conversationId.trim()).stream()
                .map(ChatHistoryConverter::toVo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long sessionId) {
        AiChatSession session = aiChatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BizException("会话不存在");
        }
        chatMemoryRepository.deleteByConversationId(session.getConversationId());
        aiChatSessionMapper.deleteById(sessionId);
    }

    private AiChatSessionAdminVo toVo(AiChatSession session) {
        AiChatSessionAdminVo vo = new AiChatSessionAdminVo();
        vo.setSessionId(session.getSessionId());
        vo.setConversationId(session.getConversationId());
        vo.setUserId(session.getUserId());
        vo.setTitle(session.getTitle());
        vo.setMessageCount(session.getMessageCount());
        vo.setCreateTime(session.getCreateTime());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }
}
