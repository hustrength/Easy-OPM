package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreparedParameterHandler implements ParameterHandler {

    private PreparedStatement preparedStatement;

    public PreparedParameterHandler(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameters(String paramType, List<String> paramOrder, Object[] parameter) throws SQLException {
        if (parameter == null) {
            return;
        }
        switch (paramType) {
            case "map": {
                CheckMapParameter.check(paramOrder, parameter);
                Map<String, Object> map = (Map<String, Object>) parameter[0];
                Object[] mappedParam = new Object[paramOrder.size()];
                for (int i = 0; i < paramOrder.size(); i++) {
                    String curPara = paramOrder.get(i);
                    AssertError.notFoundError(map.containsKey(curPara), "Set para[" + curPara + ']');
                    mappedParam[i] = map.get(curPara);
                }
                setMappedParam(preparedStatement, mappedParam);
                break;
            }
            case "basic": {
                // judge if Para1 is Number Type or String Type
                boolean paramTypeMatched = parameter.length == 1 && (parameter[0] instanceof Number || parameter[0] instanceof String);
                AssertError.notMatchedError(paramTypeMatched, "Para", parameter[0].getClass().getSimpleName(), "paraType", "basic");

                setMappedParam(preparedStatement, parameter);
                break;
            }
            default: {
                Object[] mappedParam = new Object[paramOrder.size()];
                // if given para is not matched Class, exceptions will be thrown
                try {
                    Class<?> entityClass = Class.forName(paramType);
                    for (int i = 0; i < mappedParam.length; i++) {
                        Field field = entityClass.getDeclaredField(paramOrder.get(i));
                        field.setAccessible(true);
                        mappedParam[i] = field.get(parameter[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setMappedParam(preparedStatement, mappedParam);
            }
        }

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
                preparedStatement.setByte(i + 1, (byte) param[i]);
            } else if (param[i] instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (boolean) param[i]);
            }
        }
    }
}
