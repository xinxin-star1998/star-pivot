package com.star.pivot.controller.system;

import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.PostBo;
import com.star.pivot.system.domain.bo.PostVO;
import com.star.pivot.system.domain.dto.PostDTO;
import com.star.pivot.system.domain.dto.PostQueryDTO;
import com.star.pivot.system.service.interfaces.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 *
 * @author stardust
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/sys/post")
@RequiredArgsConstructor
@Tag(name = "岗位管理", description = "岗位的增删改查等接口")
public class PostController {
    
    private final PostService postService;

    /**
     * 分页查询岗位列表接口
     * 
     * @param queryDTO 岗位查询参数对象
     * @return 分页的岗位列表结果
     */
    @Operation(summary = "分页查询岗位", description = "根据条件分页查询岗位列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:post:query')")
    @PostMapping("/list")
    public Result<PageResponse<PostVO>> list(@RequestBody PostQueryDTO queryDTO) {
        PageResponse<PostVO> page = postService.selectPostPage(queryDTO);
        return Result.success(page);
    }
    /**
     * 查询所有岗位简洁列表接口
     */
    @Operation(summary = "查询岗位简洁列表", description = "获取所有岗位的简洁信息列表，用于下拉选择")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:post:query')")
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
    @Operation(summary = "查询所有岗位", description = "获取所有岗位的完整信息列表（不分页）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @PreAuthorize("hasAuthority('system:post:query')")
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
    @Operation(summary = "获取岗位详情", description = "根据岗位ID获取岗位的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PostVO.class))),
            @ApiResponse(responseCode = "404", description = "岗位不存在")
    })
    @PreAuthorize("hasAuthority('system:post:query')")
    @GetMapping("/{postId}")
    public Result<PostVO> getInfo(@Parameter(description = "岗位ID") @PathVariable Long postId) {
        PostVO postVO = postService.selectPostById(postId);
        return Result.success(postVO);
    }

    /**
     * 新增岗位接口
     * 
     * @param postDTO 岗位数据传输对象
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "新增岗位", description = "创建新岗位")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PreAuthorize("hasAuthority('system:post:add')")
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
    @Operation(summary = "修改岗位", description = "更新岗位信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "岗位不存在")
    })
    @PreAuthorize("hasAuthority('system:post:edit')")
    @PutMapping
    public Result<?> edit(@Valid @RequestBody PostDTO postDTO) {
        boolean success = postService.updatePost(postDTO);
        return success ? Result.success("修改岗位成功") : Result.error("修改岗位失败");
    }

    /**
     * 删除岗位接口（支持单删和批量删除）
     * 
     * @param deleteRequest 删除请求，包含 ids 数组
     * @return 操作结果，成功或失败的响应
     */
    @Operation(summary = "删除岗位", description = "删除岗位（支持批量删除）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:post:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> postIds = validateIds(deleteRequest.getIds());
        boolean success = postService.deletePostByIds(postIds);
        return success ? Result.success("删除岗位成功") : Result.error("删除岗位失败");
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(
                ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}

