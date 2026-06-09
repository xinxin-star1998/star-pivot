package com.star.pivot.config.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.utils.LogUtils;
import com.star.pivot.framework.utils.StructuredLogUtils;
import com.star.pivot.system.domain.bo.LoginRequest;
import com.star.pivot.system.domain.bo.LoginResponse;
import com.star.pivot.system.domain.bo.RegisterRequest;
import com.star.pivot.system.domain.bo.RegisterResponse;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.service.impl.AsyncOperLogService;
import com.star.pivot.system.utils.LoginUser;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LogAspect {

    private static final String ANONYMOUS_PRINCIPAL = "anonymousUser";

    private final AsyncOperLogService asyncOperLogService;
    private final SysDeptMapper sysDeptMapper;
    private final ObjectMapper objectMapper;

    public LogAspect(AsyncOperLogService asyncOperLogService, SysDeptMapper sysDeptMapper, ObjectMapper objectMapper) {
        this.asyncOperLogService = asyncOperLogService;
        this.sysDeptMapper = sysDeptMapper;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.star.pivot.framework.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始新的跟踪上下文
        String traceId = StructuredLogUtils.startTrace();

        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        Object result = null;
        Log logAnnotation = null;

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            logAnnotation = method.getAnnotation(Log.class);

            setBasicInfo(operLog, logAnnotation, method, request, joinPoint);

            // 设置操作和模块信息到结构化日志上下文
            String operation = signature.getDeclaringTypeName() + "." + signature.getName();
            String module = signature.getDeclaringTypeName().split("\\.")[signature.getDeclaringTypeName().split("\\.").length - 1];
            StructuredLogUtils.setOperation(operation, module);

            result = joinPoint.proceed();
            setResponseInfo(operLog, result, logAnnotation);
            operLog.setStatus(0);
            return result;
        } catch (Exception e) {
            operLog.setStatus(1);
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                errorMsg = LogUtils.truncateString(errorMsg, 2000);
            }
            operLog.setErrorMsg(errorMsg);

            // 记录异常到结构化日志
            StructuredLogUtils.logBusinessEvent("exception_occurred", e.getMessage(),
                Map.of("exception_type", e.getClass().getSimpleName(), "operation",
                       joinPoint.getSignature().getName()));

            throw e;
        } finally {
            resolveOperatorIfAnonymous(operLog, joinPoint, result);
            long endTime = System.currentTimeMillis();
            operLog.setCostTime(endTime - startTime);
            operLog.setOperTime(LocalDateTime.now());

            // 记录性能指标到结构化日志
            StructuredLogUtils.logPerformance(joinPoint.getSignature().getName(), endTime - startTime,
                operLog.getStatus() == 0);

            // 记录操作日志到结构化日志
            Map<String, Object> params = new HashMap<>();
            if (logAnnotation.saveRequestData()) {
                Object[] filteredArgs = filterLoggableArgs(joinPoint.getArgs());
                if (filteredArgs.length > 0) {
                    Object toSerialize = filteredArgs.length == 1 ? filteredArgs[0] : filteredArgs;
                    params.put("request_params", toSerialize);
                }
            }

            StructuredLogUtils.logOperation(joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(), params, result, operLog.getStatus() == 0);

            // 异步保存操作日志，不阻塞主线程
            asyncOperLogService.saveOperLogAsync(operLog);

            // 清理MDC上下文
            StructuredLogUtils.clearContext();
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
        operLog.setTitle(LogUtils.truncateString(title, 50));
        operLog.setBusinessType(logAnnotation.businessType());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        operLog.setMethod(LogUtils.truncateString(className + "." + methodName + "()", 200));

        if (request != null) {
            operLog.setRequestMethod(LogUtils.truncateString(request.getMethod(), 10));
            operLog.setOperUrl(LogUtils.truncateString(request.getRequestURI(), 255));
            operLog.setOperIp(LogUtils.truncateString(LogUtils.getClientIp(request), 128));
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
            if (auth instanceof AnonymousAuthenticationToken) {
                operLog.setOperName(ANONYMOUS_PRINCIPAL);
                operLog.setOperatorType(0);
                return;
            }
            Object principal = auth.getPrincipal();
            if (!(principal instanceof LoginUser loginUser)) {
                String name = principal instanceof String ? (String) principal : "未知";
                if (ANONYMOUS_PRINCIPAL.equals(name)) {
                    operLog.setOperName(ANONYMOUS_PRINCIPAL);
                } else {
                    operLog.setOperName(name);
                }
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
                        operLog.setDeptName(LogUtils.truncateString(dept.getDeptName(), 50));
                    }
                }
                operLog.setOperName(LogUtils.truncateString(user.getUserName(), 50));
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

    /**
     * 登录/注册等未认证接口在记录操作日志时 SecurityContext 仍为匿名用户，
     * 从请求参数或响应结果中补全实际操作账号。
     */
    private void resolveOperatorIfAnonymous(SysOperLog operLog, JoinPoint joinPoint, Object result) {
        if (!isAnonymousOperName(operLog.getOperName())) {
            return;
        }
        String username = extractUsernameFromArgs(joinPoint.getArgs());
        if (!StringUtils.hasText(username)) {
            username = extractUsernameFromResult(result);
        }
        if (StringUtils.hasText(username)) {
            operLog.setOperName(LogUtils.truncateString(username, 50));
            operLog.setOperatorType(0);
        }
    }

    private boolean isAnonymousOperName(String operName) {
        return !StringUtils.hasText(operName)
                || ANONYMOUS_PRINCIPAL.equals(operName)
                || "匿名用户".equals(operName)
                || "未知".equals(operName);
    }

    private String extractUsernameFromArgs(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof LoginRequest loginRequest) {
                return loginRequest.getUsername();
            }
            if (arg instanceof RegisterRequest registerRequest) {
                return registerRequest.getUsername();
            }
        }
        return null;
    }

    private String extractUsernameFromResult(Object result) {
        if (result == null) {
            return null;
        }
        Object data = result;
        if (result instanceof Result<?> wrapped && wrapped.getData() != null) {
            data = wrapped.getData();
        }
        if (data instanceof LoginResponse loginResponse) {
            return loginResponse.getUsername();
        }
        if (data instanceof RegisterResponse registerResponse) {
            return registerResponse.getUsername();
        }
        return null;
    }

    private void setRequestParams(SysOperLog operLog, JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Object[] filteredArgs = filterLoggableArgs(args);
                if (filteredArgs.length > 0) {
                    Object toSerialize = filteredArgs.length == 1 ? filteredArgs[0] : filteredArgs;
                    String params = objectMapper.writeValueAsString(toSerialize);
                    params = LogUtils.desensitizeParam(params);
                    params = LogUtils.truncateString(params, 2000);
                    operLog.setOperParam(params);
                }
            }
        } catch (Exception e) {
            log.warn("记录请求参数失败", e);
            operLog.setOperParam("参数解析失败");
        }
    }

    private Object[] filterLoggableArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg != null)
                .filter(arg -> !(arg instanceof MultipartFile))
                .filter(arg -> !(arg instanceof ServletRequest))
                .filter(arg -> !(arg instanceof ServletResponse))
                .toArray();
    }

    private void setResponseInfo(SysOperLog operLog, Object result, Log logAnnotation) {
        if (logAnnotation.saveResponseData() && result != null) {
            try {
                String jsonResult = LogUtils.toJsonString(result);
                operLog.setJsonResult(LogUtils.truncateString(jsonResult, 2000));
            } catch (Exception e) {
                log.warn("记录响应结果失败", e);
                operLog.setJsonResult("响应解析失败");
            }
        }
    }

}
