package com.star.pivot.framework.exception.handler;

import com.star.pivot.framework.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ExceptionHandlerRegistry {
    
    private final List<ExceptionHandler<?>> handlers;

    public ExceptionHandlerRegistry(List<ExceptionHandler<?>> handlers) {
        this.handlers = handlers.stream()
            .sorted(Comparator.comparingInt(ExceptionHandler::getOrder))
            .toList();
        log.info("注册了 {} 个异常处理器", this.handlers.size());
    }

    @SuppressWarnings("unchecked")
    public Optional<Result<Void>> handle(Exception exception) {
        for (ExceptionHandler<?> handler : handlers) {
            if (handler.supports(exception)) {
                try {
                    Result<Void> result = ((ExceptionHandler<Exception>) handler).handle(exception);
                    log.debug("异常被处理器 {} 处理: {}", 
                        handler.getClass().getSimpleName(), exception.getClass().getSimpleName());
                    return Optional.of(result);
                } catch (Exception e) {
                    log.error("异常处理器执行失败: {}", handler.getClass().getSimpleName(), e);
                }
            }
        }
        return Optional.empty();
    }
}
