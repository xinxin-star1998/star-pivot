package com.star.pivot.controller.file;

import com.star.pivot.file.domain.dto.SysFileShareUnlockDTO;
import com.star.pivot.file.domain.vo.SysFileSharePublicVo;
import com.star.pivot.file.service.ISysFileShareService;
import com.star.pivot.framework.domain.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公开分享接口（无需登录）。
 */
@RestController
@RequestMapping("/file/share/public")
@RequiredArgsConstructor
@Tag(name = "文件分享公开", description = "外链访问")
public class SysFileSharePublicController {

    private final ISysFileShareService sysFileShareService;

    @GetMapping("/{shareCode}")
    public Result<SysFileSharePublicVo> meta(@PathVariable String shareCode) {
        return Result.success(sysFileShareService.meta(shareCode));
    }

    @PostMapping("/{shareCode}/unlock")
    public Result<SysFileSharePublicVo> unlock(
            @PathVariable String shareCode,
            @RequestBody(required = false) SysFileShareUnlockDTO dto) {
        String password = dto != null ? dto.getPassword() : null;
        return Result.success(sysFileShareService.unlock(shareCode, password));
    }
}
