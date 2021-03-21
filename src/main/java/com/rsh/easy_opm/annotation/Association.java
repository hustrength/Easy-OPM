package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

/**
 * NOTICE: Association Node is not allowed to iterate in Annotation setting but do in XML setting.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Association {

    /**
     * Returns the property name for applying this mapping.
     */
    String property() default "";

    /**
     * Returns the type of united class
     */
    Class<?> ofType();

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
