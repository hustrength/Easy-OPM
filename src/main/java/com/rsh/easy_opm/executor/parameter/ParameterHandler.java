package com.rsh.easy_opm.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterHandler {
    void setParameters(PreparedStatement ps) throws SQLException;

}
