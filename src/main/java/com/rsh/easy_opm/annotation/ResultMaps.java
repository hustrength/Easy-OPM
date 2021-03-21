package com.rsh.easy_opm.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMaps {
    ResultMap[] value();
}
