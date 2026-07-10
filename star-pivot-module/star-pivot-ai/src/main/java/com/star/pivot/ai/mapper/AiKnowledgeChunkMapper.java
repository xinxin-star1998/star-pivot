package com.star.pivot.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.pivot.ai.domain.entity.AiKnowledgeChunk;
import com.star.pivot.ai.domain.vo.AiKnowledgeChunkHitVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiKnowledgeChunkMapper extends BaseMapper<AiKnowledgeChunk> {

    List<AiKnowledgeChunkHitVo> searchFulltext(@Param("query") String query, @Param("topK") int topK);

    List<AiKnowledgeChunkHitVo> searchLike(@Param("keyword") String keyword, @Param("topK") int topK);

    List<AiKnowledgeChunkHitVo> listEmbeddableChunkBatch(
            @Param("lastChunkId") Long lastChunkId, @Param("limit") int limit);

    int insertBatch(@Param("list") List<AiKnowledgeChunk> list);
}
