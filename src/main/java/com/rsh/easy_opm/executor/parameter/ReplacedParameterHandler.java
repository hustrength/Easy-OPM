package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacedParameterHandler implements ParameterHandler {
    String queryStr;

    public ReplacedParameterHandler(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getQueryStr() {
        return queryStr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object setParameters(String paramType, List<String> paramOrder, Object[] parameter) throws SQLException {
        // when paramOrder is null, there is no replaced params
        if (paramOrder == null)
            return queryStr;
        // when paramOrder is not null, the paramType must be map
        AssertError.notMatchedError(paramType.equals("map"), "Using replaced params ${...}, paramType", paramType, "specified paramType", "map");

        CheckParameter.checkMap(parameter);
        CheckParameter.checkBasic(parameter);
        Map<String, Object> map = (Map<String, Object>) parameter[0];
        String pattern = "\\$\\{([^#{}]*)}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(queryStr);
        while (m.find()) {
            queryStr = queryStr.replaceFirst(pattern, String.valueOf(map.get(m.group(1))));
        }
        return queryStr;
    }
}
