package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultsId {
    /**
     * Return the column name(or column label) to map to this argument.
     */
    String column() default "";

    /**
     * Returns the property name for applying this mapping.
     */
    String property() default "";
}
