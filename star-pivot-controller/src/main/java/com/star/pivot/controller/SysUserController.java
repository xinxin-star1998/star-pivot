package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysUserService;
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
    public Result<PageResponse<SysUser>> pageList(@RequestBody UserReqBo userReqBo) {
        PageResponse<SysUser> pageResponse = sysUserService.pageList(userReqBo);
        return Result.success(pageResponse);
    }

    /**
     * 根据用户ID获取用户详情接口
     * 
     * @param userId 用户ID
     * @return 指定ID的用户详细信息
     */
    @GetMapping("/{userId}")
    public Result<SysUser> getUserById(@PathVariable("userId") Long userId) {
        SysUser user = sysUserService.getById(userId);
        return Result.success(user);
    }

    /**
     * 新增用户接口
     * 
     * @param user 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @PostMapping
    public Result<?> addUser(@RequestBody SysUser user) {
        boolean success = sysUserService.save(user);
        return success ? Result.success("新增用户成功") : Result.error("新增用户失败");
    }

    /**
     * 修改用户接口
     * 
     * @param user 用户数据对象
     * @return 操作结果，成功或失败的响应
     */
    @PutMapping
    public Result<?> updateUser(@RequestBody SysUser user) {
        boolean success = sysUserService.updateById(user);
        return success ? Result.success("修改用户成功") : Result.error("修改用户失败");
    }

    /**
     * 删除用户接口
     * 
     * @param userIds 用户ID数组
     * @return 操作结果，成功或失败的响应
     */
    @DeleteMapping("/{userIds}")
    public Result<?> deleteUser(@PathVariable("userIds") Long[] userIds) {
        boolean success = sysUserService.removeByIds(List.of(userIds));
        return success ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }
}

