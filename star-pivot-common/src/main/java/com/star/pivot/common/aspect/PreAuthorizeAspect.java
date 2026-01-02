//package com.star.pivot.common.aspect;
//
//import com.stardust.common.annotation.PreAuthorize;
//import com.stardust.common.exception.BusinessException;
//import com.stardust.security.PermissionService;
//import com.stardust.utils.SecurityContextUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
///**
// * 权限控制切面
// *
// * @author stardust
// * @date 2024-01-01
// */
//@Slf4j
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class PreAuthorizeAspect {
//
//    private final PermissionService permissionService;
//
//    /**
//     * 配置织入点
//     */
//    @Pointcut("@annotation(com.stardust.common.annotation.PreAuthorize)")
//    public void preAuthorizePointCut() {
//    }
//
//    /**
//     * 权限验证
//     */
//    @Before("preAuthorizePointCut()")
//    public void doBefore(JoinPoint joinPoint) {
//        try {
//            // 获取注解信息
//            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//            Method method = signature.getMethod();
//            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
//
//            if (preAuthorize != null) {
//                String permission = preAuthorize.value();
//                boolean required = preAuthorize.required();
//
//                // 如果权限标识为空，跳过权限检查
//                if (permission == null || permission.trim().isEmpty()) {
//                    return;
//                }
//
//                // 获取当前用户ID
//                Long userId = SecurityContextUtils.getUserId();
//                if (userId == null) {
//                    throw new BusinessException("未登录或登录已过期，请重新登录");
//                }
//
//                // 检查权限
//                if (!permissionService.hasPermission(userId, permission)) {
//                    if (required) {
//                        throw new BusinessException("没有访问权限，请联系管理员授权");
//                    } else {
//                        log.debug("用户没有权限: {}, 但该权限不是必需的", permission);
//                    }
//                }
//            }
//        } catch (BusinessException e) {
//            throw e;
//        } catch (Exception e) {
//            log.error("权限验证失败: {}", e.getMessage());
//            throw new BusinessException("权限验证失败");
//        }
//    }
//}
//
