package com.rsh.easy_opm.executor.parameter;

import java.util.List;

public class CqlPreparedParameter extends PreparedParameterHandler {
    public Object[] prepare(String paramType, List<String> paramOrder, Object[] parameter) {
        Object[] mappedParam = super.setParameters(paramType, paramOrder, parameter);
        Object[] cqlParam = new Object[mappedParam.length * 2];
        for (int i = 0; i < cqlParam.length; i += 2) {
            cqlParam[i] = paramOrder.get(i / 2);
            cqlParam[i + 1] = mappedParam[i / 2];
        }
        return cqlParam;
    }
}
