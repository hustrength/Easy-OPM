package com.rsh.easy_opm.reflection;

import com.rsh.easy_opm.config.MappedStatement;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

public class ReflectionUtil {
    @SuppressWarnings("unchecked")
    public static <E> Object convertToBean(MappedStatement ms, ResultSet resultSet) {
        Map<String, String> resultMap = ms.getResultMap();
        boolean mapped = resultMap != null;
        try {
            Class<?> entityClass = Class.forName(ms.getResultType());
            E entity = (E) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                String mappedName = mapResult(mapped, resultMap, fieldName);
                switch (field.getType().getSimpleName()) {
                    case "String":
                        field.set(entity, resultSet.getString(mappedName));
                        break;
                    case "int":
                        field.set(entity, resultSet.getInt(mappedName));
                        break;
                    case "boolean":
                        field.set(entity, resultSet.getBoolean(mappedName));
                        break;
                    case "float":
                        field.set(entity, resultSet.getFloat(mappedName));
                        break;
                    case "char":
                        field.set(entity, resultSet.getByte(mappedName));
                    default:
                        assert false : "Type[" + field.getType().getSimpleName() + "] is not supported";
                }
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String mapResult(boolean mapped, Map<String, String> resultMap, String fieldName) {
        if (!mapped)
            return fieldName;
        if (resultMap.containsKey(fieldName))
            return resultMap.get(fieldName);
        else return fieldName;
    }
}
