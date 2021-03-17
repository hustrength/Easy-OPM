package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultMap {
    /**
     * Returns result map names to use.
     *
     * @return result map names
     */
    String value();
}
