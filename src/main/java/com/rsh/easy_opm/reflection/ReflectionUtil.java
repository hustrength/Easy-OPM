package com.rsh.easy_opm.reflection;

import com.rsh.easy_opm.config.MappedStatement;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ReflectionUtil {
    private static Map<String, String> resultMap;
    private static ResultSet resultSet;

    @SuppressWarnings("unchecked")
    public static <T> Object convertToBean(MappedStatement ms, ResultSet result) {
        resultMap = ms.getResultMap();
        resultSet = result;
        try {
            Class<T> entityClass = (Class<T>) Class.forName(ms.getResultType());
            T entity = (T) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName();
                String mappedName = mapResult(fieldName);

                setField(field, mappedName, entity);
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String mapResult(String fieldName) {
        if (resultMap == null)
            return fieldName;
        if (resultMap.containsKey(fieldName))
            return resultMap.get(fieldName);
        else return fieldName;
    }

    private static boolean existColumn(String columnName) {
        try {
            if (resultSet.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private static <T> void setField(Field field, String mappedName, T entity) throws Exception {
        field.setAccessible(true);

        // do not set the field if not existing in resultSet
        String fieldType = field.getType().getSimpleName();
        switch (fieldType) {
            case "Date":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getDate(mappedName));
                    break;
            case "String":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getString(mappedName));
                break;
            case "int":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getInt(mappedName));
                break;
            case "boolean":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getBoolean(mappedName));
                break;
            case "float":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getFloat(mappedName));
                break;
            case "char":
                if (existColumn(mappedName))
                    field.set(entity, resultSet.getByte(mappedName));
                break;
            default:
                T entityValue = setEntity(field.getType().getName());
                field.set(entity, entityValue);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T setEntity(String classType) {
        try {
            Class<T> entityClass = (Class<T>) Class.forName(classType);
            T entity = (T) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName() + '@' + classType;
                String mappedName = mapResult(fieldName);

                setField(field, mappedName, entity);
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
