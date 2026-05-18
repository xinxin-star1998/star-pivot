package com.star.pivot.framework.datasource.aspect;

import com.star.pivot.framework.datasource.DataSourceContextHolder;
import com.star.pivot.framework.datasource.annotation.DS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

/**
 * 根据 {@link DS} 切换数据源；需在事务之前执行，故优先级较高。
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourceRouteAspect {

    @Around("execution(public * *(..)) && (@within(com.star.pivot.framework.datasource.annotation.DS) "
            + "|| @annotation(com.star.pivot.framework.datasource.annotation.DS))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DS methodDs =
                AnnotationUtils.findAnnotation(((MethodSignature) point.getSignature()).getMethod(), DS.class);
        DS classDs = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(point.getTarget()), DS.class);
        DS ds = methodDs != null ? methodDs : classDs;
        if (ds == null || ds.value().isEmpty()) {
            return point.proceed();
        }
        DataSourceContextHolder.push(ds.value());
        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.pop();
        }
    }
}
