package com.star.pivot.controller.file;

import com.star.pivot.file.domain.dto.SysFileShareCreateDTO;
import com.star.pivot.file.domain.vo.SysFileShareVo;
import com.star.pivot.file.service.ISysFileShareService;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.utils.DataScopeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file/share")
@RequiredArgsConstructor
@Tag(name = "文件分享", description = "分享外链管理")
public class SysFileShareController {

    private final ISysFileShareService sysFileShareService;
    private final DataScopeService dataScopeService;

    @Value("${file-center.share.public-base-url:}")
    private String configuredPublicBaseUrl;

    @Log(title = "创建文件分享", businessType = AppConstants.BusinessType.INSERT)
    @PreAuthorize("hasAuthority('file:resource:share')")
    @PostMapping
    public Result<SysFileShareVo> create(@Valid @RequestBody SysFileShareCreateDTO dto,
                                         HttpServletRequest request) {
        return Result.success(sysFileShareService.create(
                dto, dataScopeService.getCurrentUserDataScope(), resolvePublicBaseUrl(request)));
    }

    @PreAuthorize("hasAuthority('file:resource:share')")
    @GetMapping("/file/{fileId}")
    public Result<List<SysFileShareVo>> listByFile(@PathVariable Long fileId, HttpServletRequest request) {
        return Result.success(sysFileShareService.listByFile(
                fileId, dataScopeService.getCurrentUserDataScope(), resolvePublicBaseUrl(request)));
    }

    @PreAuthorize("hasAuthority('file:resource:share')")
    @GetMapping("/mine")
    public Result<List<SysFileShareVo>> listMine(HttpServletRequest request) {
        return Result.success(sysFileShareService.listMine(resolvePublicBaseUrl(request)));
    }

    @Log(title = "取消文件分享", businessType = AppConstants.BusinessType.DELETE)
    @PreAuthorize("hasAuthority('file:resource:share')")
    @DeleteMapping("/{shareId}")
    public Result<Void> revoke(@PathVariable Long shareId) {
        sysFileShareService.revoke(shareId);
        return Result.success();
    }

    private String resolvePublicBaseUrl(HttpServletRequest request) {
        if (StringUtils.hasText(configuredPublicBaseUrl)) {
            return configuredPublicBaseUrl;
        }
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (!StringUtils.hasText(scheme)) {
            scheme = request.getScheme();
        }
        String host = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.hasText(host)) {
            host = request.getHeader("Host");
        }
        if (!StringUtils.hasText(host)) {
            host = request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443
                    ? "" : ":" + request.getServerPort());
        }
        // 前端独立端口时，默认指向当前 Origin 由前端拼；此处给后端直链兜底
        return scheme + "://" + host;
    }
}
