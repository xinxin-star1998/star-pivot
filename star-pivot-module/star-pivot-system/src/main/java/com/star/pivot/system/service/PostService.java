package com.star.pivot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.system.domain.bo.PostBo;
import com.star.pivot.system.domain.bo.PostVO;
import com.star.pivot.system.domain.dto.PostDTO;
import com.star.pivot.system.domain.dto.PostQueryDTO;
import com.star.pivot.system.domain.entity.SysPost;

import java.util.List;

/**
 * 岗位服务接口
 *
 * @author stardust
 * @since 2024-01-01
 */
public interface PostService extends IService<SysPost> {

    /**
     * 分页查询岗位列表
     *
     * @param queryDTO 查询条件
     * @return 岗位分页列表
     */
    PageResponse<PostVO> selectPostPage(PostQueryDTO queryDTO);

    /**
     * 根据岗位ID查询岗位详情
     *
     * @param postId 岗位ID
     * @return 岗位详情
     */
    PostVO selectPostById(Long postId);

    /**
     * 新增岗位
     *
     * @param postDTO 岗位信息
     * @return 是否成功
     */
    boolean insertPost(PostDTO postDTO);

    /**
     * 修改岗位
     *
     * @param postDTO 岗位信息
     * @return 是否成功
     */
    boolean updatePost(PostDTO postDTO);

    /**
     * 删除岗位（支持单删和批量删除）
     *
     * @param postIds 岗位ID列表
     * @return 是否成功
     */
    boolean deletePostByIds(List<Long> postIds);

    /**
     * 检查岗位编码是否唯一
     *
     * @param postCode 岗位编码
     * @param postId 岗位ID（修改时传入）
     * @return 是否唯一
     */
    boolean checkPostCodeUnique(String postCode, Long postId);

    List<PostBo> selectPost();

    List<PostVO> all();
}

