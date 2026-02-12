package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BusinessException;
import com.star.pivot.system.domain.bo.PostBo;
import com.star.pivot.system.domain.bo.PostVO;
import com.star.pivot.system.domain.dto.PostDTO;
import com.star.pivot.system.domain.dto.PostQueryDTO;
import com.star.pivot.system.domain.entity.SysPost;
import com.star.pivot.system.domain.entity.UserPost;
import com.star.pivot.system.mapper.PostMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import com.star.pivot.system.service.PostService;
import com.star.pivot.security.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 *
 * @author stardust
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, SysPost> implements PostService {

    private final PostMapper postMapper;
    private final UserPostMapper userPostMapper;

    @Override
    public PageResponse<PostVO> selectPostPage(PostQueryDTO queryDTO) {
        Page<SysPost> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.like(StringUtils.hasText(queryDTO.getPostCode()), SysPost::getPostCode, queryDTO.getPostCode())
                .like(StringUtils.hasText(queryDTO.getPostName()), SysPost::getPostName, queryDTO.getPostName())
                .eq(StringUtils.hasText(queryDTO.getStatus()), SysPost::getStatus, queryDTO.getStatus())
                .orderByAsc(SysPost::getPostSort);
        IPage<SysPost> postPage = this.page(page, wrapper);
        // 转换为VO
        IPage<PostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<PostVO> voList = postPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        PageResponse<PostVO> pageResponse = new PageResponse<>();
        pageResponse.setTotal(voPage.getTotal());
        pageResponse.setRows(voPage.getRecords());
        pageResponse.setPageNum(Long.valueOf(queryDTO.getPageNum()));
        pageResponse.setPageSize(Long.valueOf(queryDTO.getPageSize()));
        return pageResponse;
    }

    @Override
    public PostVO selectPostById(Long postId) {
        SysPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException("岗位不存在");
        }
        return convertToVO(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertPost(PostDTO postDTO) {
        // 检查岗位编码是否唯一
        if (!checkPostCodeUnique(postDTO.getPostCode(), null)) {
            throw new BusinessException("岗位编码已存在");
        }

        // 创建岗位
        SysPost post = new SysPost();
        BeanUtils.copyProperties(postDTO, post);
        post.setPostSort(postDTO.getPostSort() != null ? postDTO.getPostSort() : 0);
        post.setStatus(StringUtils.hasText(postDTO.getStatus()) ? postDTO.getStatus() : "0");

        String currentUser = SecurityContextUtils.getUsername();
        post.setCreateBy(currentUser);
        post.setCreateTime(LocalDateTime.now());

        return this.save(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(PostDTO postDTO) {
        SysPost post = this.getById(postDTO.getPostId());
        if (post == null) {
            throw new BusinessException("岗位不存在");
        }

        // 检查岗位编码是否唯一
        if (!checkPostCodeUnique(postDTO.getPostCode(), postDTO.getPostId())) {
            throw new BusinessException("岗位编码已被使用");
        }

        // 更新岗位信息
        BeanUtils.copyProperties(postDTO, post, "postId");
        String currentUser = SecurityContextUtils.getUsername();
        post.setUpdateBy(currentUser);
        post.setUpdateTime(LocalDateTime.now());

        return this.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePostByIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return false;
        }
        
        for (Long postId : postIds) {
            SysPost post = this.getById(postId);
            if (post != null) {
                // 检查是否有用户使用该岗位
                LambdaQueryWrapper<UserPost> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserPost::getPostId, postId);
                long count = userPostMapper.selectCount(wrapper);
                if (count > 0) {
                    throw new BusinessException("岗位[" + post.getPostName() + "]已被使用，不能删除");
                }
                
                this.removeById(postId);
            }
        }
        return true;
    }

    @Override
    public boolean checkPostCodeUnique(String postCode, Long postId) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostCode, postCode);
        if (postId != null) {
            wrapper.ne(SysPost::getPostId, postId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public List<PostBo> selectPost() {
        List<PostBo> postVOList;
        QueryWrapper<SysPost> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "0");
        wrapper.orderByAsc("post_sort");
        List<SysPost> postList = postMapper.selectList(wrapper);
        postVOList = postList.stream()
                .map(post -> {
                    PostBo postVO = new PostBo();
                    BeanUtils.copyProperties(post, postVO);
                    return postVO;
                })
                .toList();
        return postVOList;
    }

    @Override
    public List<PostVO> all() {
        List<PostVO> postVOList = new ArrayList<>();
        List<SysPost> list= postMapper.selectList(null);
        BeanUtils.copyProperties(list,postVOList);
        return postVOList;
    }

    /**
     * 转换为VO
     */
    private PostVO convertToVO(SysPost post) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);
        return vo;
    }
}

