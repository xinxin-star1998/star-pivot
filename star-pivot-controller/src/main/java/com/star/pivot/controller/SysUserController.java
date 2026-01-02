package com.star.pivot.controller;

import com.star.pivot.common.domain.PageResponse;
import com.star.pivot.common.domain.Result;
import com.star.pivot.system.domain.bo.UserReqBo;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.service.SysUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

