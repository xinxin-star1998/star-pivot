//package com.star.pivot.common.aspect;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.lang.reflect.Method;
//import java.time.LocalDateTime;
//
///**
// * 操作日志切面
// *
// * @author stardust
// * @date 2024-01-01
// */
//@Slf4j
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class LogAspect {
//
//    private final OperLogService operLogService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private ThreadLocal<Long> startTime = new ThreadLocal<>();
//    private ThreadLocal<OperLog> operLogThreadLocal = new ThreadLocal<>();
//    private ThreadLocal<Log> logAnnotationThreadLocal = new ThreadLocal<>();
//
//    /**
//     * 配置织入点
//     */
//    @Pointcut("@annotation(com.stardust.common.annotation.Log)")
//    public void logPointCut() {
//    }
//
//    /**
//     * 处理请求前执行
//     */
//    @Before("logPointCut()")
//    public void doBefore(JoinPoint joinPoint) {
//        startTime.set(System.currentTimeMillis());
//
//        try {
//            // 获取注解信息
//            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//            Method method = signature.getMethod();
//            Log logAnnotation = method.getAnnotation(Log.class);
//
//            // 获取请求信息
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attributes == null) {
//                return;
//            }
//
//            HttpServletRequest request = attributes.getRequest();
//
//            // 创建操作日志对象
//            OperLog operLog = new OperLog();
//            operLog.setTitle(logAnnotation.title());
//            operLog.setBusinessType(logAnnotation.businessType());
//            operLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());
//            operLog.setRequestMethod(request.getMethod());
//            operLog.setOperatorType(1); // 后台用户
//            operLog.setOperUrl(request.getRequestURI());
//            operLog.setOperIp(getIpAddr(request));
//            operLog.setOperTime(LocalDateTime.now());
//
//            // 获取操作人信息
//            String operName = SecurityContextUtils.getUsername();
//            if (operName != null) {
//                operLog.setOperName(operName);
//            }
//
//            // 保存请求参数
//            if (logAnnotation.saveRequestData()) {
//                Object[] args = joinPoint.getArgs();
//                if (args != null && args.length > 0) {
//                    try {
//                        String params = objectMapper.writeValueAsString(args);
//                        operLog.setOperParam(params.length() > 2000 ? params.substring(0, 2000) : params);
//                    } catch (Exception e) {
//                        log.error("记录操作日志-保存请求参数失败: {}", e.getMessage());
//                    }
//                }
//            }
//
//            operLogThreadLocal.set(operLog);
//            logAnnotationThreadLocal.set(logAnnotation);
//        } catch (Exception e) {
//            log.error("记录操作日志-前置处理失败: {}", e.getMessage());
//        }
//    }
//
//    /**
//     * 处理完请求后执行
//     */
//    @AfterReturning(pointcut = "logPointCut()", returning = "result")
//    public void doAfterReturning(Object result) {
//        try {
//            OperLog operLog = operLogThreadLocal.get();
//            if (operLog != null) {
//                // 计算耗时
//                Long costTime = System.currentTimeMillis() - startTime.get();
//                operLog.setCostTime(costTime);
//                operLog.setStatus(0); // 正常
//
//                // 保存响应参数
//                Log logAnnotation = logAnnotationThreadLocal.get();
//                if (logAnnotation != null && logAnnotation.saveResponseData() && result != null) {
//                    try {
//                        String jsonResult = objectMapper.writeValueAsString(result);
//                        operLog.setJsonResult(jsonResult.length() > 2000 ? jsonResult.substring(0, 2000) : jsonResult);
//                    } catch (Exception e) {
//                        log.error("记录操作日志-保存响应参数失败: {}", e.getMessage());
//                    }
//                }
//
//                operLogService.recordOperLog(operLog);
//            }
//        } catch (Exception e) {
//            log.error("记录操作日志-后置处理失败: {}", e.getMessage());
//        } finally {
//            operLogThreadLocal.remove();
//            startTime.remove();
//        }
//    }
//
//    /**
//     * 异常处理
//     */
//    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
//    public void doAfterThrowing(Throwable e) {
//        try {
//            OperLog operLog = operLogThreadLocal.get();
//            if (operLog != null) {
//                operLog.setStatus(1); // 异常
//                operLog.setErrorMsg(e.getMessage() != null && e.getMessage().length() > 2000
//                    ? e.getMessage().substring(0, 2000) : e.getMessage());
//                operLog.setCostTime(System.currentTimeMillis() - startTime.get());
//
//                operLogService.recordOperLog(operLog);
//            }
//        } catch (Exception ex) {
//            log.error("记录操作日志-异常处理失败: {}", ex.getMessage());
//        } finally {
//            operLogThreadLocal.remove();
//            logAnnotationThreadLocal.remove();
//            startTime.remove();
//        }
//    }
//
//    /**
//     * 获取客户端IP地址
//     */
//    private String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
//}
//
