package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultType {
    /**
     * Returns the return type or the return type of the element in a collection.
     */
    Class<?> value();
}
