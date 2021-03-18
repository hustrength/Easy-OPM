package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamType {
    /**
     * @return the type of parameters
     */
    Class<?> value();
}
