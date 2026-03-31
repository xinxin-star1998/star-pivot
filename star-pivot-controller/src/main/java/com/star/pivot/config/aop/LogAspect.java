package com.star.pivot.config.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.service.SysOperLogService;
import com.star.pivot.system.utils.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LogAspect {

    private final SysOperLogService sysOperLogService;
    private final SysDeptMapper sysDeptMapper;
    private final ObjectMapper objectMapper;

    public LogAspect(SysOperLogService sysOperLogService, SysDeptMapper sysDeptMapper, ObjectMapper objectMapper) {
        this.sysOperLogService = sysOperLogService;
        this.sysDeptMapper = sysDeptMapper;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.star.pivot.framework.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        Object result;

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            setBasicInfo(operLog, logAnnotation, method, request, joinPoint);
            result = joinPoint.proceed();
            setResponseInfo(operLog, result, logAnnotation);
            operLog.setStatus(0);
            return result;
        } catch (Exception e) {
            operLog.setStatus(1);
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                errorMsg = truncateString(errorMsg, 2000);
            }
            operLog.setErrorMsg(errorMsg);
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            operLog.setCostTime(endTime - startTime);
            operLog.setOperTime(LocalDateTime.now());
            try {
                sysOperLogService.saveOperLog(operLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    private void setBasicInfo(SysOperLog operLog, Log logAnnotation, Method method,
                              HttpServletRequest request, JoinPoint joinPoint) {
        String title = logAnnotation.title();
        if (!StringUtils.hasText(title)) {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = method.getName();
            title = className + "." + methodName;
        }
        operLog.setTitle(truncateString(title, 50));
        operLog.setBusinessType(logAnnotation.businessType());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        operLog.setMethod(truncateString(className + "." + methodName + "()", 200));

        if (request != null) {
            operLog.setRequestMethod(truncateString(request.getMethod(), 10));
            operLog.setOperUrl(truncateString(request.getRequestURI(), 255));
            operLog.setOperIp(truncateString(LogUtils.getClientIp(request), 128));
        }

        setOperatorInfo(operLog);

        if (logAnnotation.saveRequestData() && request != null) {
            setRequestParams(operLog, joinPoint);
        }
    }

    private void setOperatorInfo(SysOperLog operLog) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) {
                operLog.setOperName("匿名用户");
                operLog.setOperatorType(0);
                return;
            }
            Object principal = auth.getPrincipal();
            if (!(principal instanceof LoginUser loginUser)) {
                operLog.setOperName(principal instanceof String ? (String) principal : "未知");
                operLog.setOperatorType(0);
                return;
            }
            if (loginUser.getUser() != null) {
                SysUser user = loginUser.getUser();
                operLog.setOperName(user.getUserName());
                operLog.setOperatorType(1);
                if (user.getDeptId() != null) {
                    SysDept dept = sysDeptMapper.selectById(user.getDeptId());
                    if (dept != null) {
                        operLog.setDeptName(truncateString(dept.getDeptName(), 50));
                    }
                }
                operLog.setOperName(truncateString(user.getUserName(), 50));
            } else {
                operLog.setOperName("匿名用户");
                operLog.setOperatorType(0);
            }
        } catch (Exception e) {
            log.warn("获取操作人员信息失败", e);
            operLog.setOperName("未知");
            operLog.setOperatorType(0);
        }
    }

    private void setRequestParams(SysOperLog operLog, JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String params = objectMapper.writeValueAsString(args);
                params = LogUtils.desensitizeParam(params);
                params = truncateString(params, 2000);
                operLog.setOperParam(params);
            }
        } catch (Exception e) {
            log.warn("记录请求参数失败", e);
            operLog.setOperParam("参数解析失败");
        }
    }

    private void setResponseInfo(SysOperLog operLog, Object result, Log logAnnotation) {
        if (logAnnotation.saveResponseData() && result != null) {
            try {
                String jsonResult = LogUtils.toJsonString(result);
                operLog.setJsonResult(truncateString(jsonResult, 2000));
            } catch (Exception e) {
                log.warn("记录响应结果失败", e);
                operLog.setJsonResult("响应解析失败");
            }
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.length() <= maxLength) {
            byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (bytes.length <= maxLength) {
                return str;
            }
        }
        int targetLength = Math.min(str.length(), maxLength);
        String truncated = str.substring(0, targetLength);
        byte[] bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        while (bytes.length > maxLength - 10 && targetLength > 0) {
            targetLength--;
            truncated = str.substring(0, targetLength);
            bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        return truncated + "...";
    }
}
