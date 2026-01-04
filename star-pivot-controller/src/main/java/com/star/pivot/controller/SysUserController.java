package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.bo.UserVO;
import com.star.pivot.system.domain.dto.ResetPasswordDTO;
import com.star.pivot.system.domain.dto.UserDTO;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysUserService;
import com.star.pivot.system.utils.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息表(SysUser)表控制层
 *
 * @author xinxin
 * @since 2025-12-28 17:28:11
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    /**
     * 服务对象
     */
    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 分页查询用户
     *
     * @param userReqBo 用户查询参数
     * @return 分页结果
     */
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
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable("userId") Long userId) {
        UserVO userVO = sysUserService.selectByUserId(userId);
        return Result.success(userVO);
    }

    /**
     * 新增用户接口
     * 
     * @param userDTO 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @PostMapping("add")
    public Result<?> addUser(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.addUser(userDTO);
        return success ? Result.success("新增用户成功") : Result.error("新增用户失败");
    }

    /**
     * 修改用户接口
     * 
     * @param userDTO 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @PostMapping("update")
    public Result<?> updateUser(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.updateUser(userDTO);
        return success ? Result.success("修改用户成功") : Result.error("修改用户失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public Result<?> remove(@PathVariable Long[] userIds) {
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
    @PutMapping("/resetPwd")
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
    @PostMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody UserDTO userDTO) {
        boolean success = sysUserService.changeUserStatus(userDTO.getUserId(), userDTO.getStatus());
        return success ? Result.success("修改状态成功") : Result.error("修改状态失败");
    }
}

