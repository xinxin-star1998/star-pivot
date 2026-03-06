package com.star.pivot.framework.exception.handler;

import com.star.pivot.framework.domain.Result;

public interface ExceptionHandler<T extends Exception> {
    
    boolean supports(Exception exception);
    
    Result handle(T exception);
    
    default int getOrder() {
        return 100;
    }
}
