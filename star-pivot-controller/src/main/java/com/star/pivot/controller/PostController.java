package com.star.pivot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.PostBo;
import com.star.pivot.system.domain.bo.PostVO;
import com.star.pivot.system.domain.dto.PostDTO;
import com.star.pivot.system.domain.dto.PostQueryDTO;
import com.star.pivot.system.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 *
 * @author stardust
 * @date 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/sys/post")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;

    /**
     * 分页查询岗位列表接口
     * 
     * @param queryDTO 岗位查询参数对象
     * @return 分页的岗位列表结果
     */
    @PostMapping("/list")
    public Result<PageResponse<PostVO>> list(@RequestBody PostQueryDTO queryDTO) {
        PageResponse<PostVO> page = postService.selectPostPage(queryDTO);
        return Result.success(page);
    }
    /**
     * 查询所有岗位简洁列表接口
     */
    @GetMapping("/simpleList")
    public Result<List<PostBo>> simpleList() {
        List<PostBo> postBos = postService.selectPost();
        return Result.success(postBos);
    }
    /**
     * 查询所有岗位列表接口（不分页）
     * 
     * @return 所有岗位列表
     */
    @GetMapping("/all")
    public Result<List<PostVO>> all() {
        List<PostVO> page = postService.all();
        return Result.success(page);
    }

    /**
     * 根据岗位ID查询岗位详情接口
     * 
     * @param postId 岗位ID
     * @return 指定ID的岗位详细信息
     */
    @GetMapping("/{postId}")
    public Result<PostVO> getInfo(@PathVariable Long postId) {
        PostVO postVO = postService.selectPostById(postId);
        return Result.success(postVO);
    }

    /**
     * 新增岗位接口
     * 
     * @param postDTO 岗位数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody PostDTO postDTO) {
        boolean success = postService.insertPost(postDTO);
        return success ? Result.success("新增岗位成功") : Result.error("新增岗位失败");
    }

    /**
     * 修改岗位接口
     * 
     * @param postDTO 岗位数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @PutMapping
    public Result<?> edit(@Valid @RequestBody PostDTO postDTO) {
        boolean success = postService.updatePost(postDTO);
        return success ? Result.success("修改岗位成功") : Result.error("修改岗位失败");
    }

    /**
     * 删除岗位接口
     * 
     * @param postIds 岗位ID数组
     * @return 操作结果，成功或失败的响应
     */
    @DeleteMapping("/{postIds}")
    public Result<?> remove(@PathVariable Long[] postIds) {
        boolean success = postService.deletePostByIds(postIds);
        return success ? Result.success("删除岗位成功") : Result.error("删除岗位失败");
    }
}

