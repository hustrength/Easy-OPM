package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Result {
    /**
     * Return the column name(or column label) to map to this argument.
     *
     * @return the column name(or column label)
     */
    String column() default "";

    /**
     * Returns the property name for applying this mapping.
     *
     * @return the property name
     */
    String property() default "";
}
