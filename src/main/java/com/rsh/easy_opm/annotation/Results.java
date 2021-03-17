package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Results {
    /**
     * Returns the id of this result map.
     *
     * @return the id of this result map
     */
    String id() default "";

    /**
     * Returns mapping definitions for property.
     *
     * @return mapping definitions
     */
    Result[] value() default {};
}
