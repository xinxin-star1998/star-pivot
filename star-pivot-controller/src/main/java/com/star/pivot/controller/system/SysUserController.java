package com.star.pivot.controller.system;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.DeleteRequest;
import com.star.pivot.framework.domain.PageResponse;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.ResetPasswordDTO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysRole;
import com.star.pivot.system.service.AccountLockService;
import com.star.pivot.system.service.PermissionService;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.security.utils.SecurityContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户信息表(SysUser)表控制层
 *
 * @author xinxin
 * @since 2025-12-28 17:28:11
 */
@RestController
@RequestMapping("/sys/user")
@Tag(name = "用户管理", description = "用户信息的增删改查、密码重置、状态修改、账户解锁等接口")
public class SysUserController {
    /**
     * 服务对象
     */
    private final SysUserService sysUserService;
    private final AccountLockService accountLockService;
    private final PermissionService permissionService;

    public SysUserController(SysUserService sysUserService, AccountLockService accountLockService, PermissionService permissionService) {
        this.sysUserService = sysUserService;
        this.accountLockService = accountLockService;
        this.permissionService = permissionService;
    }

    /**
     * 判断当前用户是否是超级管理员
     */
    private boolean isSuperAdmin() {
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        // 超级管理员用户ID
        if (AppConstants.ADMIN_USER_ID.equals(currentUserId)) {
            return true;
        }
        // 拥有 admin 角色的用户
        return permissionService.hasRole(AppConstants.ADMIN_ROLE_KEY);
    }

    /**
     * 分页查询用户
     *
     * @param userReqBo 用户查询参数
     * @return 分页结果
     */
    @Log(title = "用户管理")
    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @PreAuthorize("hasAuthority('system:user:query')")
    @PostMapping("/pageList")
    public Result<PageResponse<UserVO>> pageList(@RequestBody UserReqBo userReqBo) {
        PageResponse<UserVO> pageResponse = sysUserService.pageList(userReqBo);
        return Result.success(pageResponse);
    }

    /**
     * 根据用户ID获取用户详情接口
     * 
     * @param userId 用户ID
     * @return 指定ID的用户详细信息
     */
    @Log(title = "用户管理")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@Parameter(description = "用户ID") @PathVariable("userId") Long userId) {
        UserVO userVO = sysUserService.selectByUserId(userId);
        return Result.success(userVO);
    }

    /**
     * 新增用户接口
     * 
     * @param userDTO 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @Log(title = "用户管理", businessType = 1)
    @Operation(summary = "新增用户", description = "创建新用户，需要提供用户名、密码、昵称等基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "参数错误或用户名已存在")
    })
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping("/add")
    public Result<?> addUser(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.addUser(userDTO);
        return success ? Result.success("新增用户成功") : Result.error("新增用户失败");
    }

    /**
     * 修改用户接口
     *
     * <p>权限校验：只能修改自己的用户信息
     *
     * @param userDTO 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @Log(title = "用户管理", businessType = 2)
    @Operation(summary = "修改用户", description = "更新用户信息，只能修改自己的用户信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "无权修改其他用户信息")
    })
    @PostMapping("/update")
    public Result<?> updateUser(@RequestBody UserDTO userDTO) {
        // 权限校验：只能修改自己的用户信息，超级管理员可以修改所有用户信息
        Long currentUserId = SecurityContextUtils.getUserId();
        if (currentUserId == null) {
            return Result.error("用户未登录");
        }

        // 超级管理员可以修改所有用户信息
        boolean isSuperAdmin = isSuperAdmin();
        if (!isSuperAdmin && !currentUserId.equals(userDTO.getUserId())) {
            return Result.error("只能修改自己的用户信息");
        }

        boolean success = sysUserService.updateUser(userDTO);
        return success ? Result.success("修改用户成功") : Result.error("修改用户失败");
    }

    /**
     * 删除用户（支持单删和批量删除）
     */
    @Log(title = "用户管理", businessType = 3)
    @Operation(summary = "删除用户", description = "删除用户（支持批量删除），不能删除当前登录用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "不能删除当前登录用户或删除ID为空")
    })
    @PreAuthorize("hasAuthority('system:user:delete')")
    @DeleteMapping("/delete")
    public Result<?> remove(@RequestBody DeleteRequest deleteRequest) {
        List<Long> userIds = validateIds(deleteRequest.getIds());
        
        // 不能删除自己
        Long currentUserId = SecurityContextUtils.getUserId();
        for (Long userId : userIds) {
            if (userId.equals(currentUserId)) {
                return Result.error("不能删除当前登录用户");
            }
        }

        boolean success = sysUserService.deleteUserByIds(userIds);
        return success ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }

    /**
     * 重置密码
     */
    @Log(title = "用户管理", businessType = 2)
    @Operation(summary = "重置密码", description = "重置指定用户的登录密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重置成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PostMapping("/resetPwd")
    public Result<?> resetPwd(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        boolean success = sysUserService.resetUserPassword(
                resetPasswordDTO.getUserId(),
                resetPasswordDTO.getPassword()
        );
        return success ? Result.success("重置密码成功") : Result.error("重置密码失败");
    }

    /**
     * 修改用户状态
     */
    @Operation(summary = "修改用户状态", description = "启用或禁用用户账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PostMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.changeUserStatus(userDTO.getUserId(), userDTO.getStatus());
        return success ? Result.success("修改状态成功") : Result.error("修改状态失败");
    }

    /**
     * 管理员解锁账户
     * 解除因登录失败次数过多而被锁定的账户
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @Log(title = "用户管理", businessType = 2)
    @Operation(summary = "解锁账户", description = "管理员解锁因登录失败次数过多而被锁定的账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "解锁成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasAuthority('system:user:unLock') and @ss.hasRole('admin')")
    @PostMapping("/unlock/{userId}")
    public Result<?> unlockUser(@Parameter(description = "用户ID") @PathVariable("userId") Long userId) {
        AccountLockService.UnlockResult r = accountLockService.unlockUserByUserId(userId);
        return r.isSuccess() ? Result.success(r.getMessage()) : Result.error(r.getMessage());
    }

    /**
     * 批量导入用户（Excel 导入）
     * 说明：
     * - 前端会先将 Excel 解析成 List&lt;Map&lt;String, Object&gt;&gt;
     * - 再直接调用该接口进行批量导入
     *
     * @param rowList Excel 解析后的行数据列表
     * @return 导入结果
     */
    @Log(title = "用户管理", businessType = 1)
    @Operation(summary = "批量导入用户", description = "通过Excel文件批量导入用户，前端需先将Excel解析为List<Map<String, Object>>格式")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "导入成功")
    })
    @PreAuthorize("hasAuthority('system:user:import')")
    @PostMapping("/import")
    public Result<?> importUsers(@RequestBody List<Map<String, Object>> rowList) {
        int successCount = sysUserService.importUsers(rowList);
        return Result.success("成功导入 " + successCount + " 个用户");
    }

    /**
     * 验证ID列表非空
     */
    private List<Long> validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "删除ID不能为空");
        }
        return ids;
    }
}

