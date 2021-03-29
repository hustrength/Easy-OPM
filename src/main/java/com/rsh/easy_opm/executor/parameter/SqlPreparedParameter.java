package com.rsh.easy_opm.executor.parameter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlPreparedParameter extends PreparedParameterHandler{
    private final PreparedStatement preparedStatement;

    public SqlPreparedParameter(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void prepare(String paramType, List<String> paramOrder, Object[] parameter) throws SQLException {
        Object[] mappedParam = super.setParameters(paramType, paramOrder, parameter);
        setMappedParam(preparedStatement, mappedParam);
    }

    private void setMappedParam(PreparedStatement preparedStatement, Object[] param) throws SQLException {
        for (int i = 0; i < param.length; i++) {
            if (param[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (int) param[i]);
            } else if (param[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) param[i]);
            } else if (param[i] instanceof Float) {
                preparedStatement.setFloat(i + 1, (float) param[i]);
            } else if (param[i] instanceof Character) {
                preparedStatement.setShort(i + 1, (short) param[i]);
            } else if (param[i] instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (boolean) param[i]);
            } else if (param[i] instanceof Double) {
                preparedStatement.setDouble(i + 1, (double) param[i]);
            } else if (param[i] instanceof Byte) {
                preparedStatement.setByte(i + 1, (byte) param[i]);
            } else if (param[i] instanceof Short) {
                preparedStatement.setShort(i + 1, (short) param[i]);
            } else if (param[i] instanceof Long) {
                preparedStatement.setLong(i + 1, (long) param[i]);
            } else if (param[i] instanceof Date) {
                preparedStatement.setDate(i + 1, (Date) param[i]);
            }
        }
    }
}
