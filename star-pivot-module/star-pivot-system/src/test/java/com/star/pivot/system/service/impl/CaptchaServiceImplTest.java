package com.star.pivot.system.service.impl;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.startsWith;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CaptchaServiceImplTest {

    @Mock
    private RedisCache redisCache;

    @Mock
    private ISysConfigService sysConfigService;

    private CaptchaServiceImpl captchaService;

    @BeforeEach
    void setUp() {
        when(sysConfigService.getCaptchaLength()).thenReturn(4);
        when(sysConfigService.getCaptchaExpireSeconds()).thenReturn(180);
        captchaService = new CaptchaServiceImpl(redisCache, sysConfigService);
    }

    @Test
    void testGenerateCaptcha_ReturnsValidResponse() {
        // 测试生成验证码返回有效响应
        
        // 执行
        CaptchaIssueResponse response = captchaService.generateCaptcha("login");

        // 验证
        assertNotNull(response);
        assertNotNull(response.getCaptchaToken());
        assertNotNull(response.getCaptchaImage());
        assertTrue(response.getCaptchaImage().startsWith("data:image/jpeg;base64,"));
        assertFalse(response.getCaptchaToken().isEmpty());

        // 验证 Redis 被调用存储验证码状态
        verify(redisCache, times(1)).setCacheObject(
            anyString(), 
            any(CaptchaState.class),
            anyLong(), 
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testGenerateCaptcha_GeneratesUniqueTokens() {
        // 测试每次生成的 token 都是唯一的
        
        CaptchaIssueResponse response1 = captchaService.generateCaptcha("login");
        CaptchaIssueResponse response2 = captchaService.generateCaptcha("login");

        assertNotEquals(response1.getCaptchaToken(), response2.getCaptchaToken());
        
        // 验证 Redis 被调用了两次
        verify(redisCache, times(2)).setCacheObject(
            anyString(), 
            any(CaptchaState.class),
            anyLong(), 
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testVerifyCaptcha_Success_WhenCodeMatches() {
        // 模拟 Redis 返回有效的验证码状态
        CaptchaState state = createCaptchaState("abcd", "login");
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        // 创建验证请求 - 输入小写字母
        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("test-token");
        request.setCode("ABCD"); // 大写输入，应该大小写不敏感
        request.setScene("login");

        // 执行验证 - 应该成功
        var response = assertDoesNotThrow(() -> captchaService.verifyCaptcha(request));
        
        // 验证返回了 proof
        assertNotNull(response);
        assertNotNull(response.getCaptchaProof());
        assertFalse(response.getCaptchaProof().isEmpty());

        // 验证 Redis 删除了验证码状态（一次性使用）
        verify(redisCache, times(1)).deleteObject(anyString());
        
        // 验证 Redis 存储了 proof
        verify(redisCache, times(1)).setCacheObject(
            startsWith("captcha:proof:"),
            eq("login"),
            anyLong(),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testVerifyCaptcha_CaseInsensitive() {
        // 测试验证码校验大小写不敏感
        
        // 模拟原始验证码是 "ABCD"（在 Redis 中存储的是小写哈希）
        CaptchaState state = createCaptchaState("abcd", "login");
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        // 测试各种大小写组合都应该成功
        String[] testInputs = {"abcd", "ABCD", "AbCd", "aBcD"};
        
        for (String input : testInputs) {
            CaptchaVerifyRequest request = new CaptchaVerifyRequest();
            request.setCaptchaToken("test-token");
            request.setCode(input);
            request.setScene("login");

            assertDoesNotThrow(() -> captchaService.verifyCaptcha(request),
                "验证码校验应该对大小写不敏感，输入: " + input);
        }
    }

    @Test
    void testVerifyCaptcha_Fails_WhenCodeMismatch() {
        // 测试验证码错误时抛出异常
        
        CaptchaState state = createCaptchaState("abcd", "login");
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("test-token");
        request.setCode("wrong-code");
        request.setScene("login");

        BizException exception = assertThrows(BizException.class,
            () -> captchaService.verifyCaptcha(request));
        
        assertEquals(ErrorCode.CAPTCHA_ERROR.getCode(), exception.getCode());
        
        // 验证尝试次数增加
        verify(redisCache, times(1)).setCacheObject(
            anyString(), 
            any(CaptchaState.class),
            anyLong(), 
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testVerifyCaptcha_Fails_WhenExpired() {
        // 测试验证码过期时抛出异常
        
        when(redisCache.getCacheObject(anyString())).thenReturn(null);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("expired-token");
        request.setCode("abcd");
        request.setScene("login");

        BizException exception = assertThrows(BizException.class,
            () -> captchaService.verifyCaptcha(request));
        
        assertEquals(ErrorCode.CAPTCHA_EXPIRED.getCode(), exception.getCode());
    }

    @Test
    void testVerifyCaptcha_Fails_WhenTooManyAttempts() {
        // 测试超过最大尝试次数时抛出异常
        
        CaptchaState state = createCaptchaState("abcd", "login");
        state.setAttempts(5); // 已达到最大尝试次数
        when(redisCache.getCacheObject(anyString())).thenReturn(state);

        CaptchaVerifyRequest request = new CaptchaVerifyRequest();
        request.setCaptchaToken("test-token");
        request.setCode("wrong");
        request.setScene("login");

        BizException exception = assertThrows(BizException.class,
            () -> captchaService.verifyCaptcha(request));
        
        assertEquals(ErrorCode.CAPTCHA_TOO_MANY_ATTEMPTS.getCode(), exception.getCode());
        
        // 验证 Redis 删除了验证码状态
        verify(redisCache, times(1)).deleteObject(anyString());
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Success() {
        // 测试 proof 验证成功
        
        String proof = "valid-proof";
        String scene = "login";
        when(redisCache.getCacheObject("captcha:proof:" + proof)).thenReturn(scene);

        boolean result = captchaService.validateAndConsumeCaptchaProof(proof, scene);
        
        assertTrue(result);
        
        // 验证 proof 被消费（删除）
        verify(redisCache, times(1)).deleteObject("captcha:proof:" + proof);
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenInvalidProof() {
        // 测试无效 proof 验证失败
        
        when(redisCache.getCacheObject(anyString())).thenReturn(null);

        boolean result = captchaService.validateAndConsumeCaptchaProof("invalid-proof", "login");
        
        assertFalse(result);
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenSceneMismatch() {
        // 测试场景不匹配时验证失败
        
        String proof = "valid-proof";
        when(redisCache.getCacheObject("captcha:proof:" + proof)).thenReturn("register");

        boolean result = captchaService.validateAndConsumeCaptchaProof(proof, "login");
        
        assertFalse(result);
        
        // 即使失败，proof 也应该被删除（一次性使用）
        verify(redisCache, times(1)).deleteObject("captcha:proof:" + proof);
    }

    @Test
    void testValidateAndConsumeCaptchaProof_Fails_WhenNullProof() {
        // 测试 null proof 验证失败
        
        boolean result = captchaService.validateAndConsumeCaptchaProof(null, "login");
        
        assertFalse(result);
        
        // 不应该调用 Redis
        verify(redisCache, never()).getCacheObject(anyString());
    }

    /**
     * 辅助方法：创建验证码状态对象
     * @param code 验证码明文（会被转换为小写并计算哈希）
     * @param scene 业务场景
     */
    private CaptchaState createCaptchaState(String code, String scene) {
        try {
            // 使用反射调用私有方法 hashCode，或者手动创建
            CaptchaState state = new CaptchaState();
            
            // 由于 hashCode 是私有方法，我们直接设置一个固定的哈希值用于测试
            // 实际测试中，验证码 "abcd" 的哈希应该是固定的
            String salt = "test-salt";
            String hash = computeTestHash(code.toLowerCase(), salt);
            
            state.setCodeHash(hash);
            state.setSalt(salt);
            state.setAttempts(0);
            state.setMaxAttempts(5);
            state.setScene(scene);
            
            return state;
        } catch (Exception e) {
            throw new RuntimeException("创建测试验证码状态失败", e);
        }
    }

    /**
     * 计算测试用的哈希值（与 CaptchaServiceImpl 中的 hashCode 方法保持一致）
     */
    private String computeTestHash(String code, String salt) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            String raw = code + ":" + salt;
            byte[] hash = digest.digest(raw.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        }
    }
}