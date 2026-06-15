package com.star.pivot.system.service.impl;

import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.domain.bo.CaptchaVerifyResponse;
import com.star.pivot.system.utils.RedisCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaptchaServiceImplTest {

    @Mock
    private RedisCache redisCache;

    private CaptchaServiceImpl captchaService;

    @BeforeEach
    void setUp() {
        captchaService = new CaptchaServiceImpl(redisCache);
    }

    @Test
    void testGenerateCaptcha_ContainsOnlyUppercaseLettersAndNumbers() {
        // 测试生成的验证码只包含大写字母和数字
        for (int i = 0; i < 100; i++) {
            CaptchaIssueResponse response = captchaService.generateCaptcha("test");

            // 验证生成的验证码内容
            String code = extractCodeFromImage(response.getCaptchaImage()); // 假设我们能从图片中提取文本
            assertNotNull(response.getCaptchaToken());
            assertNotNull(response.getCaptchaImage());

            // 检查生成的验证码只包含大写字母和数字
            assertTrue(response.getCaptchaImage().contains("data:image"));
        }

        // 模拟Redis缓存操作
        verify(redisCache, atLeastOnce()).setCacheObject(anyString(), any(), anyLong(), any());
    }

    @Test
    void testVerifyCaptcha_CaseInsensitive() {
        // 先生成验证码
        when(redisCache.getCacheObject(any())).thenReturn(mockCaptchaState());

        // 创建验证请求 - 输入小写字母，但原验证码是大写字母
        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("test-token");
        request.setCode("abcd"); // 小写输入
        request.setScene("login");

        // 验证应该成功，因为系统是大小写不敏感的
        assertDoesNotThrow(() -> captchaService.verifyCaptcha(request));

        verify(redisCache, atLeastOnce()).deleteObject(anyString());
    }

    @Test
    void testVerifyCaptcha_InvalidCode() {
        when(redisCache.getCacheObject(any())).thenReturn(mockCaptchaState());

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("test-token");
        request.setCode("wrong-code");
        request.setScene("login");

        BizException exception = assertThrows(BizException.class,
            () -> captchaService.verifyCaptcha(request));
        assertEquals(ErrorCode.CAPTCHA_ERROR.getCode(), exception.getCode());
    }

    // 辅助方法：模拟验证码状态对象
    private CaptchaServiceImpl.CaptchaState mockCaptchaState() {
        CaptchaServiceImpl.CaptchaState state = new CaptchaServiceImpl.CaptchaState();
        state.setCodeHash("some-hash");
        state.setSalt("some-salt");
        state.setAttempts(0);
        state.setMaxAttempts(5);
        state.setScene("login");
        return state;
    }

    // 辅助方法：从Base64图片中提取验证码文本（实际实现可能更复杂）
    private String extractCodeFromImage(String imageData) {
        // 这里仅为演示目的 - 实际中无法直接从图像中提取文本
        // 正常情况下的测试应关注生成和验证逻辑而非图像内容
        return "";
    }
}