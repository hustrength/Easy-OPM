package com.rsh.easy_opm.executor.parameter;

import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class PreparedParameterHandler implements ParameterHandler {

    @SuppressWarnings("unchecked")
    @Override
    public Object[] setParameters(String paramType, List<String> paramOrder, Object[] parameter) {
        if (parameter == null) {
            return null;
        }
        Object[] mappedParam;
        switch (paramType) {
            case "map": {
                CheckParameter.checkMap(parameter);
                Map<String, Object> map = (Map<String, Object>) parameter[0];
                mappedParam = new Object[paramOrder.size()];
                for (int i = 0; i < paramOrder.size(); i++) {
                    String curPara = paramOrder.get(i);
                    AssertError.notFoundError(map.containsKey(curPara), "Set para[" + curPara + ']');
                    mappedParam[i] = map.get(curPara);
                }
                break;
            }
            case "basic": {
                CheckParameter.checkBasic(parameter);
                mappedParam = new Object[]{parameter[0]};
                break;
            }
            default: {
                mappedParam = new Object[paramOrder.size()];
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
            }
        }
        return mappedParam;
    }
}
