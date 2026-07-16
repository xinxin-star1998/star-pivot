package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.pivot.file.config.FileCenterProperties;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.domain.dto.SysFileTagBindDTO;
import com.star.pivot.file.domain.dto.SysFileTagDTO;
import com.star.pivot.file.domain.entity.*;
import com.star.pivot.file.domain.vo.SysFileTagVo;
import com.star.pivot.file.domain.vo.SysFileVo;
import com.star.pivot.file.mapper.*;
import com.star.pivot.file.service.ISysFileMetaService;
import com.star.pivot.file.support.FileDataScopeSupport;
import com.star.pivot.framework.domain.DataScope;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysFileMetaServiceImpl implements ISysFileMetaService {

    private final SysFileMapper sysFileMapper;
    private final SysFileFavoriteMapper favoriteMapper;
    private final SysFileRecentMapper recentMapper;
    private final SysFileTagMapper tagMapper;
    private final SysFileTagRelMapper tagRelMapper;
    private final FileCenterProperties fileCenterProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long fileId, DataScope dataScope) {
        SysFile file = requireAccessibleActive(fileId, dataScope);
        Long userId = requireUserId();
        SysFileFavorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<SysFileFavorite>()
                .eq(SysFileFavorite::getUserId, userId)
                .eq(SysFileFavorite::getFileId, file.getFileId())
                .last("LIMIT 1"));
        if (existing != null) {
            favoriteMapper.deleteById(existing.getFavoriteId());
            return false;
        }
        SysFileFavorite fav = new SysFileFavorite();
        fav.setUserId(userId);
        fav.setFileId(file.getFileId());
        fav.setCreateBy(SecurityContextUtils.getUsername());
        fav.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(fav);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void touchRecent(Long fileId, DataScope dataScope) {
        SysFile file = requireAccessibleActive(fileId, dataScope);
        Long userId = requireUserId();
        recentMapper.upsert(userId, file.getFileId());
        int keep = Math.max(10, fileCenterProperties.getDownload().getRecentKeep());
        List<Long> overflow = recentMapper.selectOverflowRecentIds(userId, keep);
        if (!CollectionUtils.isEmpty(overflow)) {
            recentMapper.deleteBatchIds(overflow);
        }
    }

    @Override
    public List<SysFileTagVo> listMyTags() {
        Long userId = requireUserId();
        return tagMapper.selectList(new LambdaQueryWrapper<SysFileTag>()
                        .eq(SysFileTag::getCreateByUserId, userId)
                        .orderByAsc(SysFileTag::getTagName))
                .stream()
                .map(this::toTagVo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileTagVo createTag(SysFileTagDTO dto) {
        Long userId = requireUserId();
        String name = dto.getTagName().trim();
        long exists = tagMapper.selectCount(new LambdaQueryWrapper<SysFileTag>()
                .eq(SysFileTag::getCreateByUserId, userId)
                .eq(SysFileTag::getTagName, name));
        if (exists > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "标签已存在");
        }
        SysFileTag tag = new SysFileTag();
        tag.setTagName(name);
        tag.setTagColor(StringUtils.hasText(dto.getTagColor()) ? dto.getTagColor() : "#409EFF");
        tag.setCreateByUserId(userId);
        tag.setCreateBy(SecurityContextUtils.getUsername());
        tag.setCreateTime(LocalDateTime.now());
        tag.setRemark(dto.getRemark());
        tagMapper.insert(tag);
        return toTagVo(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileTagVo updateTag(SysFileTagDTO dto) {
        AssertUtils.notNull(dto.getTagId(), ErrorCode.PARAM_INVALID, "标签ID不能为空");
        SysFileTag tag = requireMyTag(dto.getTagId());
        String name = dto.getTagName().trim();
        long exists = tagMapper.selectCount(new LambdaQueryWrapper<SysFileTag>()
                .eq(SysFileTag::getCreateByUserId, tag.getCreateByUserId())
                .eq(SysFileTag::getTagName, name)
                .ne(SysFileTag::getTagId, tag.getTagId()));
        if (exists > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "标签已存在");
        }
        tag.setTagName(name);
        if (StringUtils.hasText(dto.getTagColor())) {
            tag.setTagColor(dto.getTagColor());
        }
        tag.setRemark(dto.getRemark());
        tag.setUpdateBy(SecurityContextUtils.getUsername());
        tag.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(tag);
        return toTagVo(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        SysFileTag tag = requireMyTag(tagId);
        tagRelMapper.delete(new LambdaQueryWrapper<SysFileTagRel>().eq(SysFileTagRel::getTagId, tag.getTagId()));
        tagMapper.deleteById(tag.getTagId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindTags(SysFileTagBindDTO dto, DataScope dataScope) {
        Long userId = requireUserId();
        List<SysFileTag> tags = loadMyTags(dto.getTagIds(), userId);
        if (tags.size() != new HashSet<>(dto.getTagIds()).size()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "存在无权使用的标签");
        }
        LocalDateTime now = LocalDateTime.now();
        for (Long fileId : dto.getFileIds()) {
            SysFile file = requireAccessibleActive(fileId, dataScope);
            for (SysFileTag tag : tags) {
                long exists = tagRelMapper.selectCount(new LambdaQueryWrapper<SysFileTagRel>()
                        .eq(SysFileTagRel::getTagId, tag.getTagId())
                        .eq(SysFileTagRel::getFileId, file.getFileId()));
                if (exists > 0) {
                    continue;
                }
                SysFileTagRel rel = new SysFileTagRel();
                rel.setTagId(tag.getTagId());
                rel.setFileId(file.getFileId());
                rel.setCreateByUserId(userId);
                rel.setCreateTime(now);
                tagRelMapper.insert(rel);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindTags(SysFileTagBindDTO dto, DataScope dataScope) {
        Long userId = requireUserId();
        loadMyTags(dto.getTagIds(), userId);
        for (Long fileId : dto.getFileIds()) {
            requireAccessibleActive(fileId, dataScope);
            tagRelMapper.delete(new LambdaQueryWrapper<SysFileTagRel>()
                    .eq(SysFileTagRel::getCreateByUserId, userId)
                    .eq(SysFileTagRel::getFileId, fileId)
                    .in(SysFileTagRel::getTagId, dto.getTagIds()));
        }
    }

    @Override
    public void enrichList(List<SysFileVo> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            rows.forEach(r -> {
                r.setFavorited(false);
                r.setTags(List.of());
            });
            return;
        }
        List<Long> fileIds = rows.stream().map(SysFileVo::getFileId).filter(Objects::nonNull).toList();
        if (fileIds.isEmpty()) {
            return;
        }
        Set<Long> favored = new HashSet<>(favoriteMapper.selectFileIdsByUserAndFileIds(userId, fileIds));
        Map<Long, List<SysFileTagVo>> tagMap = tagRelMapper.selectTagsByFileIds(userId, fileIds).stream()
                .collect(Collectors.groupingBy(SysFileTagVo::getFileId));
        for (SysFileVo row : rows) {
            row.setFavorited(favored.contains(row.getFileId()));
            row.setTags(tagMap.getOrDefault(row.getFileId(), List.of()));
        }
    }

    @Override
    public Map<String, Object> favoriteStatus(Long fileId) {
        Long userId = requireUserId();
        long count = favoriteMapper.selectCount(new LambdaQueryWrapper<SysFileFavorite>()
                .eq(SysFileFavorite::getUserId, userId)
                .eq(SysFileFavorite::getFileId, fileId));
        return Map.of("favorited", count > 0);
    }

    private SysFile requireAccessibleActive(Long fileId, DataScope dataScope) {
        SysFile file = sysFileMapper.selectById(fileId);
        AssertUtils.notNull(file, ErrorCode.NOT_FOUND, "文件不存在");
        if (!FileBizConstants.DEL_FLAG_NORMAL.equals(file.getDelFlag())) {
            throw new BizException(ErrorCode.NOT_FOUND, "文件不存在或已删除");
        }
        if (!FileDataScopeSupport.isAccessible(file, dataScope)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权访问该文件");
        }
        return file;
    }

    private SysFileTag requireMyTag(Long tagId) {
        Long userId = requireUserId();
        SysFileTag tag = tagMapper.selectById(tagId);
        AssertUtils.notNull(tag, ErrorCode.NOT_FOUND, "标签不存在");
        if (!Objects.equals(tag.getCreateByUserId(), userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该标签");
        }
        return tag;
    }

    private List<SysFileTag> loadMyTags(List<Long> tagIds, Long userId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return List.of();
        }
        return tagMapper.selectList(new LambdaQueryWrapper<SysFileTag>()
                .eq(SysFileTag::getCreateByUserId, userId)
                .in(SysFileTag::getTagId, tagIds));
    }

    private Long requireUserId() {
        Long userId = SecurityContextUtils.getUserId();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }

    private SysFileTagVo toTagVo(SysFileTag tag) {
        SysFileTagVo vo = new SysFileTagVo();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
