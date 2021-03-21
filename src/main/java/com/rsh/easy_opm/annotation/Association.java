package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Association {

    /**
     * Returns the property name for applying this mapping.
     */
    String property() default "";

    /**
     * Designate another SQL to provide the parameter of this SQL
     */
    String select() default "";

    /**
     * The column name of the designated another SQL
     */
    String column() default "";

    /**
     * Returns mapping definitions for union.
     */
    Result[] result() default {};
}
