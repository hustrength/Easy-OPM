package com.rsh.easy_opm.executor.parameter;

import java.sql.SQLException;
import java.util.List;

public interface ParameterHandler {
    Object setParameters(String paramType, List<String> paramOrder, Object[] parameter) throws SQLException;
}
