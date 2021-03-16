package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.util.List;
import java.util.Map;

public class CheckMapParameter {
    public static void check(List<String> paramOrder, Object[] parameter){
        // judge if Param1 is Map Type
        boolean paramTypeMatched = parameter.length == 1 && parameter[0] instanceof Map;
        AssertError.notMatchedError(paramTypeMatched, "Para", "1", "paraType", "map");

//        // judge if given paramNum is correct
//        Map<String, Object> map = (Map<String, Object>) parameter[0];
//        boolean paramNumMatched = paramOrder.size() == map.size();
//        AssertError.notMatchedError(paramNumMatched, "Set paraNum", String.valueOf(paramOrder.size()), "Given map paraNum", String.valueOf(map.size()));
    }
}
