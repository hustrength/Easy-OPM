package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    /**
     * Returns an SQL for retrieving record(s).
     *
     * @return an SQL for retrieving record(s)
     */
    String sql();

    /**
     * @return the type of parameters
     */
    String paramType();
}
