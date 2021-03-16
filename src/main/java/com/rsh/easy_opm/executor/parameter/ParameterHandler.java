package com.rsh.easy_opm.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface ParameterHandler {
//    void setParameters(PreparedStatement ps) throws SQLException;

    void setParameters(String paramType, List<String> paramOrder, Object[] parameter) throws SQLException;
}
