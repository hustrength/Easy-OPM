package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GdResultType {
    /**
     * Returns the actual type of entity in graph database
     */
    Class<?> gdType();
}
