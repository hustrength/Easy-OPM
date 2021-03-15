package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DefaultParameterHandler implements ParameterHandler {
    private final String paraType;
    private final List<String> paraOrder;
    private final Object[] parameter;

    public DefaultParameterHandler(String paraType, List<String> paraOrder, Object[] parameter) {
        this.paraType = paraType;
        this.paraOrder = paraOrder;
        this.parameter = parameter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameters(PreparedStatement preparedStatement) throws SQLException {
        if (parameter == null) {
            return;
        }
        if (paraType == null)
            ;
        switch (paraType) {
            case "map": {
                // judge if Para1 is Map Type
                boolean paraTypeMatched = parameter.length == 1 && parameter[0] instanceof Map;
                AssertError.notMatchedError(paraTypeMatched, "Para", "1", "paraType", "map");

                // judge if given paraNum is correct
                boolean paraNumMatched = paraOrder.size() == parameter.length;
                AssertError.notMatchedError(paraNumMatched, "Set paraNum", String.valueOf(paraOrder.size()), "Given paraNum", String.valueOf(parameter.length));

                Map<String, Object> map = (Map<String, Object>) parameter[0];
                Object[] mappedPara = new Object[map.size()];
                for (int i = 0; i < mappedPara.length; i++) {
                    (mappedPara)[i] = map.get(paraOrder.get(i));
                }
                setMappedPara(preparedStatement, mappedPara);
                break;
            }
            case "basic": {
                // judge if Para1 is Number Type or String Type
                boolean paraTypeMatched = parameter.length == 1 && (parameter[0] instanceof Number || parameter[0] instanceof String);
                AssertError.notMatchedError(paraTypeMatched, "Para", parameter[0].getClass().getSimpleName(), "paraType", "basic");

                setMappedPara(preparedStatement, parameter);
                break;
            }
            default: {
                Object[] mappedPara = new Object[paraOrder.size()];
                // if given para is not matched Class, exceptions will be thrown
                try {
                    Class<?> entityClass = Class.forName(paraType);
                    for (int i = 0; i < mappedPara.length; i++){
                        Field field = entityClass.getDeclaredField(paraOrder.get(i));
                        field.setAccessible(true);
                        mappedPara[i] = field.get(parameter[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setMappedPara(preparedStatement, mappedPara);
            }
        }

    }

    private void setMappedPara(PreparedStatement preparedStatement, Object[] para) throws SQLException {
        for (int i = 0; i < para.length; i++) {
            if (para[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (int) para[i]);
            } else if (para[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) para[i]);
            } else if (para[i] instanceof Float) {
                preparedStatement.setFloat(i + 1, (float) para[i]);
            } else if (para[i] instanceof Character) {
                preparedStatement.setByte(i + 1, (byte) para[i]);
            } else if (para[i] instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (boolean) para[i]);
            }
        }
    }
}
