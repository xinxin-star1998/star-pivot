package com.star.pivot.framework.exception;

import com.star.pivot.framework.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 验证工具类
 */
@Slf4j
public class ValidationHelper {

    private static final Validator validator;

    static {
        Validator tempValidator = null;
        try {
            Class<?> validatorFactoryClass = Class.forName("jakarta.validation.Validation");
            Object factory = validatorFactoryClass.getMethod("buildDefaultValidatorFactory").invoke(null);
            tempValidator = (Validator) factory.getClass().getMethod("getValidator").invoke(factory);
        } catch (Exception e) {
            log.error("初始化Validator失败", e);
        }
        validator = tempValidator;
    }

    /**
     * 验证对象
     */
    public static <T> void validate(T object, Class<?>... groups) {
        if (object == null) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "验证对象不能为空");
        }
        ensureValidatorInitialized();

        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String messageTemplate = violation.getMessage();
                    return String.format("%s: %s", propertyPath, messageTemplate);
                })
                .collect(Collectors.joining("; "));

            throw new BizException(ErrorCode.VALIDATE_ERROR, message);
        }
    }

    /**
     * 验证对象（返回错误信息，不抛出异常）
     */
    public static <T> String validateQuietly(T object, Class<?>... groups) {
        if (object == null) {
            return "验证对象不能为空";
        }
        if (validator == null) {
            return "参数校验组件未初始化";
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            return violations.stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String messageTemplate = violation.getMessage();
                    return String.format("%s: %s", propertyPath, messageTemplate);
                })
                .collect(Collectors.joining("; "));
        }

        return null;
    }

    /**
     * 验证单个条件
     */
    public static void validateCondition(boolean condition, String message) {
        if (!condition) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, message);
        }
    }

    private static void ensureValidatorInitialized() {
        if (validator == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "参数校验组件未初始化");
        }
    }

    /**
     * 验证ID
     */
    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "ID不能为空且必须大于0");
        }
    }

    /**
     * 验证字符串长度
     */
    public static void validateLength(String value, String paramName, int minLength, int maxLength) {
        if (StringUtils.isBlank(value)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "参数不能为空: " + paramName);
        }

        int length = value.length();
        if (length < minLength) {
            throw new BizException(ErrorCode.VALIDATE_ERROR,
                String.format("%s长度不能小于%d", paramName, minLength));
        }
        if (length > maxLength) {
            throw new BizException(ErrorCode.VALIDATE_ERROR,
                String.format("%s长度不能大于%d", paramName, maxLength));
        }
    }

    /**
     * 验证邮箱格式
     */
    public static void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "邮箱不能为空");
        }

        // 简单的邮箱正则验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "邮箱格式不正确");
        }
    }

    /**
     * 验证手机号格式
     */
    public static void validatePhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "手机号不能为空");
        }

        // 简单的手机号正则验证（11位数字，1开头）
        String phoneRegex = "^1[3-9]\\d{9}$";
        if (!phone.matches(phoneRegex)) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "手机号格式不正确");
        }
    }

    /**
     * 验证密码强度
     */
    public static void validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "密码不能为空");
        }

        // 密码长度至少8位
        if (password.length() < 8) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码长度至少8位");
        }

        // 包含至少一个数字
        if (!password.matches(".*\\d.*")) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码必须包含至少一个数字");
        }

        // 包含至少一个字母
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码必须包含至少一个字母");
        }
    }
}