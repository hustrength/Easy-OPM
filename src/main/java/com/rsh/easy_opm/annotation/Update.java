package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
    /**
     * Returns an SQL for retrieving record(s).
     *
     * @return an SQL for retrieving record(s)
     */
    String value();
}
