package com.star.pivot.controller.auth;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证 - 验证码管理
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理 - 验证码", description = "图形验证码获取与校验接口")
public class AuthCaptchaController {

    private final CaptchaService captchaService;

    @Operation(summary = "获取验证码", description = "生成图形验证码，返回验证码Token和Base64编码的图片")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = CaptchaIssueResponse.class))),
            @ApiResponse(responseCode = "500", description = "生成验证码失败")
    })
    @GetMapping("/captcha")
    public Result<CaptchaIssueResponse> getCaptcha(@Parameter(description = "业务场景，可选，默认login")
                                                   @RequestParam(value = "scene", required = false) String scene) {
        try {
            CaptchaIssueResponse response = captchaService.generateCaptcha(scene != null ? scene : "login");
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.error("验证码场景参数错误: {}", scene, e);
            return Result.error(400, "验证码场景参数错误");
        } catch (RuntimeException e) {
            log.error("生成验证码失败, scene={}", scene, e);
            return Result.error(500, "生成验证码失败");
        }
    }

    @Operation(summary = "校验验证码", description = "校验用户输入的验证码，验证通过后返回proof凭证（一次性使用）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "校验成功", content = @Content(schema = @Schema(implementation = CaptchaVerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "验证码错误或已过期")
    })
    @PostMapping("/captcha/verify")
    public Result<CaptchaVerifyResponse> verifyCaptcha(@RequestBody CaptchaVerifyRequest request) {
        CaptchaVerifyResponse response = captchaService.verifyCaptcha(request);
        return Result.success(response);
    }
}

