package com.star.pivot.config.aop;

import com.star.pivot.framework.annotation.ValidateParams;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

/**
 * 参数验证切面
 */
@Aspect
@Component
@Slf4j
public class ValidationAspect {

    @Around("@annotation(validateParams)")
    public Object around(ProceedingJoinPoint joinPoint, ValidateParams validateParams) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        if (paramNames == null) {
            paramNames = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                paramNames[i] = "arg" + i;
            }
        }
        Set<String> targetParamSet = toTargetParamSet(validateParams.value());

        // 验证参数
        for (int i = 0; i < paramNames.length && i < args.length; i++) {
            String paramName = paramNames[i];
            Object paramValue = args[i];

            // 检查是否需要验证该参数
            if (shouldValidate(paramName, targetParamSet)) {
                validateParameter(paramName, paramValue, validateParams);
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 判断是否需要验证该参数
     */
    private boolean shouldValidate(String paramName, Set<String> targetParams) {
        if (targetParams == null || targetParams.isEmpty()) {
            return true; // 如果没有指定参数，则验证所有参数
        }
        return targetParams.contains(paramName);
    }

    private Set<String> toTargetParamSet(String[] targetParams) {
        if (targetParams == null || targetParams.length == 0) {
            return null;
        }
        return new HashSet<>(Arrays.asList(targetParams));
    }

    /**
     * 验证参数
     */
    private void validateParameter(String paramName, Object paramValue, ValidateParams validateParams) {
        // 非空验证
        if (paramValue == null) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "参数不能为空: " + paramName);
        }

        if (paramValue instanceof String) {
            String stringValue = (String) paramValue;

            // 根据规则验证
            for (String rule : validateParams.rules()) {
                switch (rule) {
                    case "notBlank":
                        if (StringUtils.isBlank(stringValue)) {
                            throw new BizException(ErrorCode.PARAM_NOT_NULL, "参数不能为空: " + paramName);
                        }
                        break;
                    case "email":
                        if (!isValidEmail(stringValue)) {
                            throw new BizException(ErrorCode.VALIDATE_ERROR, "邮箱格式不正确: " + paramName);
                        }
                        break;
                    case "phone":
                        if (!isValidPhone(stringValue)) {
                            throw new BizException(ErrorCode.VALIDATE_ERROR, "手机号格式不正确: " + paramName);
                        }
                        break;
                    case "password":
                        validatePassword(stringValue, paramName);
                        break;
                    case "notEmpty":
                        if (stringValue.trim().isEmpty()) {
                            throw new BizException(ErrorCode.PARAM_NOT_NULL, "参数不能为空字符串: " + paramName);
                        }
                        break;
                }
            }
        }

        // 数值类型验证
        if (paramValue instanceof Number) {
            Number numberValue = (Number) paramValue;

            for (String rule : validateParams.rules()) {
                switch (rule) {
                    case "positive":
                        if (numberValue.doubleValue() <= 0) {
                            throw new BizException(ErrorCode.VALIDATE_ERROR, "参数必须大于0: " + paramName);
                        }
                        break;
                    case "nonNegative":
                        if (numberValue.doubleValue() < 0) {
                            throw new BizException(ErrorCode.VALIDATE_ERROR, "参数不能为负数: " + paramName);
                        }
                        break;
                }
            }
        }
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }

    /**
     * 验证密码强度
     */
    private void validatePassword(String password, String paramName) {
        if (StringUtils.isBlank(password)) {
            throw new BizException(ErrorCode.PARAM_NOT_NULL, "密码不能为空: " + paramName);
        }

        if (password.length() < 8) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码长度至少8位: " + paramName);
        }

        if (!password.matches(".*\\d.*")) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码必须包含至少一个数字: " + paramName);
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            throw new BizException(ErrorCode.VALIDATE_ERROR, "密码必须包含至少一个字母: " + paramName);
        }
    }
}