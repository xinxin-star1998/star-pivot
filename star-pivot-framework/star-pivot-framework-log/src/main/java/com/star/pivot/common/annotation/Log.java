package com.star.pivot.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String title() default "";

    int businessType() default 0;

    boolean saveRequestData() default true;

    boolean saveResponseData() default true;
}
