package com.rsh.easy_opm.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultParameterHandler implements ParameterHandler {
    private final Object[] parameter;

    public DefaultParameterHandler(Object[] parameter) {
        this.parameter = parameter;
    }

    @Override
    public void setParameters(PreparedStatement preparedStatement) throws SQLException {
        if (parameter == null) {
            return;
        }
        for (int i = 0; i < parameter.length; i++) {
            if (parameter[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (int) parameter[i]);
            } else if (parameter[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) parameter[i]);
            } else if (parameter[i] instanceof Float) {
                preparedStatement.setFloat(i + 1, (float) parameter[i]);
            } else if (parameter[i] instanceof Character) {
                preparedStatement.setByte(i + 1, (byte) parameter[i]);
            } else if (parameter[i] instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (boolean) parameter[i]);
            }
        }
    }
}
