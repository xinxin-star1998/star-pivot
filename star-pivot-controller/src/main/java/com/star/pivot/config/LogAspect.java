package com.star.pivot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.pivot.common.annotation.Log;
import com.star.pivot.common.utils.LogUtils;
import com.star.pivot.system.domain.entity.SysDept;
import com.star.pivot.system.domain.entity.SysOperLog;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.mapper.SysDeptMapper;
import com.star.pivot.system.service.SysOperLogService;
import com.star.pivot.system.utils.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

/**
 * 操作日志切面
 *
 * @author xinxin
 * @since 2026-01-23
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogService sysOperLogService;
    private final SysDeptMapper sysDeptMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 配置切点：拦截所有带有 @Log 注解的方法
     */
    @Pointcut("@annotation(com.star.pivot.common.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        Object result = null;

        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            // 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            // 设置基本信息
            setBasicInfo(operLog, logAnnotation, method, request, joinPoint);

            // 执行方法
            result = joinPoint.proceed();

            // 设置响应信息
            setResponseInfo(operLog, result, logAnnotation);

            // 设置操作状态为成功
            operLog.setStatus(0);

            return result;
        } catch (Exception e) {
            // 设置异常信息
            operLog.setStatus(1);
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                errorMsg = truncateString(errorMsg, 2000);
            }
            operLog.setErrorMsg(errorMsg);
            throw e;
        } finally {
            // 计算耗时
            long endTime = System.currentTimeMillis();
            operLog.setCostTime(endTime - startTime);
            operLog.setOperTime(LocalDateTime.now());

            // 异步保存日志（避免影响主流程性能）
            try {
                sysOperLogService.saveOperLog(operLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    /**
     * 设置基本信息
     */
    private void setBasicInfo(SysOperLog operLog, Log logAnnotation, Method method, 
                             HttpServletRequest request, JoinPoint joinPoint) {
        // 设置模块标题（限制长度）
        String title = logAnnotation.title();
        if (!StringUtils.hasText(title)) {
            // 如果没有指定标题，使用类名+方法名
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = method.getName();
            title = className + "." + methodName;
        }
        operLog.setTitle(truncateString(title, 50));

        // 设置业务类型
        operLog.setBusinessType(logAnnotation.businessType());

        // 设置方法名（限制长度）
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String fullMethodName = className + "." + methodName + "()";
        operLog.setMethod(truncateString(fullMethodName, 200));

        // 设置请求方式
        if (request != null) {
            operLog.setRequestMethod(truncateString(request.getMethod(), 10));
            operLog.setOperUrl(truncateString(request.getRequestURI(), 255));
            operLog.setOperIp(truncateString(LogUtils.getClientIp(request), 128));
        }

        // 设置操作人员信息
        setOperatorInfo(operLog);

        // 设置请求参数
        if (logAnnotation.saveRequestData() && request != null) {
            setRequestParams(operLog, joinPoint, request);
        }
    }

    /**
     * 设置操作人员信息
     */
    private void setOperatorInfo(SysOperLog operLog) {
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (loginUser != null && loginUser.getUser() != null) {
                SysUser user = loginUser.getUser();
                operLog.setOperName(user.getUserName());
                operLog.setOperatorType(1); // 1-后台用户

                // 查询部门名称（限制长度）
                if (user.getDeptId() != null) {
                    SysDept dept = sysDeptMapper.selectById(user.getDeptId());
                    if (dept != null) {
                        operLog.setDeptName(truncateString(dept.getDeptName(), 50));
                    }
                }
                // 限制操作人员名称长度
                operLog.setOperName(truncateString(user.getUserName(), 50));
            } else {
                // 匿名用户或未登录
                operLog.setOperName("匿名用户");
                operLog.setOperatorType(0); // 0-其它
            }
        } catch (Exception e) {
            log.warn("获取操作人员信息失败", e);
            operLog.setOperName("未知");
            operLog.setOperatorType(0);
        }
    }

    /**
     * 设置请求参数
     */
    private void setRequestParams(SysOperLog operLog, JoinPoint joinPoint, HttpServletRequest request) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 将参数转换为JSON字符串
                String params = objectMapper.writeValueAsString(args);
                // 参数脱敏
                params = LogUtils.desensitizeParam(params);
                // 限制长度（考虑UTF-8多字节字符，预留一些空间）
                params = truncateString(params, 2000);
                operLog.setOperParam(params);
            }
        } catch (Exception e) {
            log.warn("记录请求参数失败", e);
            operLog.setOperParam("参数解析失败");
        }
    }

    /**
     * 设置响应信息
     */
    private void setResponseInfo(SysOperLog operLog, Object result, Log logAnnotation) {
        if (logAnnotation.saveResponseData() && result != null) {
            try {
                String jsonResult = LogUtils.toJsonString(result);
                // 限制长度（考虑UTF-8多字节字符，预留一些空间）
                jsonResult = truncateString(jsonResult, 2000);
                operLog.setJsonResult(jsonResult);
            } catch (Exception e) {
                log.warn("记录响应结果失败", e);
                operLog.setJsonResult("响应解析失败");
            }
        }
    }

    /**
     * 截断字符串，确保不超过指定长度（考虑UTF-8多字节字符）
     *
     * @param str 原始字符串
     * @param maxLength 最大长度（字节数，varchar长度）
     * @return 截断后的字符串
     */
    private String truncateString(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 如果字符串长度（字符数）已经小于等于最大长度，直接返回
        if (str.length() <= maxLength) {
            // 但需要检查字节长度，因为UTF-8中文字符可能占用3-4个字节
            byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (bytes.length <= maxLength) {
                return str;
            }
        }
        
        // 需要截断，逐步减少字符直到字节长度符合要求
        int targetLength = Math.min(str.length(), maxLength);
        String truncated = str.substring(0, targetLength);
        byte[] bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // 如果字节长度仍然超过，继续截断
        while (bytes.length > maxLength - 10 && targetLength > 0) { // 预留10个字节给"..."
            targetLength--;
            truncated = str.substring(0, targetLength);
            bytes = truncated.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        
        return truncated + "...";
    }
}
