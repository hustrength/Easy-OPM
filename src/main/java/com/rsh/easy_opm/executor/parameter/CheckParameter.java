package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.sql.Date;
import java.util.Map;

public class CheckParameter {
    public static void checkMap(Object[] parameter){
        // judge if Param1 is Map Type
        boolean paramTypeMatched = parameter.length == 1 && parameter[0] instanceof Map;
        AssertError.notMatchedError(paramTypeMatched, "Para", "1", "paraType", "map");
    }

    public static void checkBasic(Object[] parameter){
        // judge if Para1 is Number Type or String Type
        boolean paramTypeMatched = parameter.length == 1 && (parameter[0] instanceof Number || parameter[0] instanceof String || parameter[0] instanceof Date);
        AssertError.notMatchedError(paramTypeMatched, "Para", parameter[0].getClass().getSimpleName(), "paraType", "basic");
    }
}
