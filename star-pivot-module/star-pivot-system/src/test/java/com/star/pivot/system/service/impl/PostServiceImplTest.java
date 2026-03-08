package com.star.pivot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.PostVO;
import com.star.pivot.system.domain.dto.PostDTO;
import com.star.pivot.system.domain.dto.PostQueryDTO;
import com.star.pivot.system.domain.entity.SysPost;
import com.star.pivot.system.mapper.PostMapper;
import com.star.pivot.system.mapper.UserPostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("岗位服务测试")
class PostServiceImplTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserPostMapper userPostMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private SysPost testPost;
    private PostDTO testPostDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "postMapper", postMapper);
        ReflectionTestUtils.setField(postService, "userPostMapper", userPostMapper);
        ReflectionTestUtils.setField(postService, "baseMapper", postMapper);

        testPost = new SysPost();
        testPost.setPostId(1L);
        testPost.setPostCode("CEO");
        testPost.setPostName("首席执行官");
        testPost.setPostSort(1);
        testPost.setStatus("0");
        testPost.setCreateTime(LocalDateTime.now());

        testPostDTO = new PostDTO();
        testPostDTO.setPostCode("CTO");
        testPostDTO.setPostName("首席技术官");
        testPostDTO.setPostSort(2);
        testPostDTO.setStatus("0");
    }

    @Nested
    @DisplayName("岗位查询测试")
    class QueryPostTests {

        @Test
        @DisplayName("分页查询岗位列表")
        @SuppressWarnings("unchecked")
        void selectPostPage() {
            PostQueryDTO queryDTO = new PostQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10);

            Page<SysPost> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testPost));
            page.setTotal(1);

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResponse<PostVO> result = postService.selectPostPage(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRows().size());
        }

        @Test
        @DisplayName("根据ID查询岗位 - 岗位存在")
        void selectPostById_PostExists() {
            when(postMapper.selectById(1L)).thenReturn(testPost);

            PostVO result = postService.selectPostById(1L);

            assertNotNull(result);
            assertEquals("首席执行官", result.getPostName());
        }

        @Test
        @DisplayName("根据ID查询岗位 - 岗位不存在")
        void selectPostById_PostNotExists() {
            when(postMapper.selectById(999L)).thenReturn(null);

            assertThrows(BizException.class, () -> postService.selectPostById(999L));
        }

        @Test
        @DisplayName("查询所有岗位")
        void all() {
            when(postMapper.selectList(any())).thenReturn(Arrays.asList(testPost));

            List<PostVO> result = postService.all();

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("岗位新增测试")
    class AddPostTests {

        @Test
        @DisplayName("新增岗位 - 成功")
        @SuppressWarnings("unchecked")
        void insertPost_Success() {
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(postMapper.insert(any(SysPost.class))).thenReturn(1);

            assertDoesNotThrow(() -> postService.insertPost(testPostDTO));
        }

        @Test
        @DisplayName("新增岗位 - 岗位编码已存在")
        @SuppressWarnings("unchecked")
        void insertPost_CodeExists() {
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            assertThrows(BizException.class, () -> postService.insertPost(testPostDTO));
        }
    }

    @Nested
    @DisplayName("岗位更新测试")
    class UpdatePostTests {

        @Test
        @DisplayName("更新岗位 - 成功")
        @SuppressWarnings("unchecked")
        void updatePost_Success() {
            testPostDTO.setPostId(1L);

            when(postMapper.selectById(1L)).thenReturn(testPost);
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(postMapper.updateById(any(SysPost.class))).thenReturn(1);

            assertDoesNotThrow(() -> postService.updatePost(testPostDTO));
        }

        @Test
        @DisplayName("更新岗位 - 岗位不存在")
        void updatePost_PostNotExists() {
            testPostDTO.setPostId(999L);

            when(postMapper.selectById(999L)).thenReturn(null);

            assertThrows(BizException.class, () -> postService.updatePost(testPostDTO));
        }

        @Test
        @DisplayName("更新岗位 - 岗位编码已被使用")
        @SuppressWarnings("unchecked")
        void updatePost_CodeInUse() {
            testPostDTO.setPostId(1L);

            when(postMapper.selectById(1L)).thenReturn(testPost);
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            assertThrows(BizException.class, () -> postService.updatePost(testPostDTO));
        }
    }

    @Nested
    @DisplayName("岗位删除测试")
    class DeletePostTests {

        @Test
        @DisplayName("删除岗位 - 成功")
        @SuppressWarnings("unchecked")
        void deletePostByIds_Success() {
            when(postMapper.selectById(1L)).thenReturn(testPost);
            when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(postMapper.deleteById(1L)).thenReturn(1);

            List<Long> postIds = Arrays.asList(1L);

            boolean result = postService.deletePostByIds(postIds);

            assertTrue(result);
        }

        @Test
        @DisplayName("删除岗位 - 岗位已被使用")
        @SuppressWarnings("unchecked")
        void deletePostByIds_PostInUse() {
            when(postMapper.selectById(1L)).thenReturn(testPost);
            when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

            List<Long> postIds = Arrays.asList(1L);

            assertThrows(BizException.class, () -> postService.deletePostByIds(postIds));
        }

        @Test
        @DisplayName("删除岗位 - 空列表")
        void deletePostByIds_EmptyList() {
            List<Long> postIds = Collections.emptyList();

            boolean result = postService.deletePostByIds(postIds);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("岗位校验测试")
    class ValidatePostTests {

        @Test
        @DisplayName("检查岗位编码唯一性 - 唯一")
        @SuppressWarnings("unchecked")
        void checkPostCodeUnique_Unique() {
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            boolean result = postService.checkPostCodeUnique("CEO", null);

            assertTrue(result);
        }

        @Test
        @DisplayName("检查岗位编码唯一性 - 不唯一")
        @SuppressWarnings("unchecked")
        void checkPostCodeUnique_NotUnique() {
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            boolean result = postService.checkPostCodeUnique("CEO", null);

            assertFalse(result);
        }

        @Test
        @DisplayName("检查岗位编码唯一性 - 排除自身")
        @SuppressWarnings("unchecked")
        void checkPostCodeUnique_ExcludeSelf() {
            when(postMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            boolean result = postService.checkPostCodeUnique("CEO", 1L);

            assertTrue(result);
        }
    }
}
