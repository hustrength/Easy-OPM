package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ResultMaps.class)
public @interface ResultMap {
    /**
     * Returns the id of this result map.
     */
    String id();

    /**
     * Returns mapping definitions for property.
     */
    Result[] result() default {};

    /**
     * Returns collection properties to unite one-to-many class.<br/>
     * <br/>
     * NOTICE: Only the 1st Collection Node will be parsed.
     */
    Collection[] collection() default {};

    /**
     * Returns collection properties to unite many-to-one or one-to-one class.
     */
    Association[] association() default {};

    /**
     * The primary key of result mapping
     */
    ResultsId[] idNode() default {};
}

