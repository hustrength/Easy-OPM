package com.rsh.easy_opm.reflection;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReflectionUtil {
    @SuppressWarnings("unchecked")
    public static <T> Object convertToBean(MappedStatement ms, ResultSet resultSet) {
        Map<String, String> resultMap = ms.getResultMap();
        boolean mapped = resultMap != null;
        try {
            Class<T> entityClass = (Class<T>) Class.forName(ms.getResultType());
            T entity = (T) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName();
                String mappedName = mapResult(mapped, resultMap, fieldName);

                setField(field, ms, mappedName, resultSet, entity);
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String mapResult(boolean mapped, Map<String, String> resultMap, String fieldName) {
        if (!mapped)
            return fieldName;
        if (resultMap.containsKey(fieldName))
            return resultMap.get(fieldName);
        else return fieldName;
    }

    private static boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T> T setEntity(MappedStatement ms, ResultSet resultSet, String classType) {
        List<String> classChian = ms.getResultMapClassChain();
        if (classChian == null)
            return null;
        AssertError.notMatchedError(classType.equals(classChian.get(0)), "Member Type", classType, "Type in resultMap", classChian.get(0));

        Map<String, String> resultMap = ms.getResultMap();
        try {
            Class<T> entityClass = (Class<T>) Class.forName(classType);
            T entity = (T) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName() + '@' + classType;
                String mappedName = mapResult(true, resultMap, fieldName);

                setField(field, ms, mappedName, resultSet, entity);
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void setField(Field field, MappedStatement ms, String mappedName, ResultSet resultSet, T entity) throws Exception{
        field.setAccessible(true);

        // do not set the field if not existing in resultSet
        String fieldType = field.getType().getSimpleName();
        switch (fieldType) {
            case "String":
                if (isExistColumn(resultSet, mappedName))
                    field.set(entity, resultSet.getString(mappedName));
                break;
            case "int":
                if (isExistColumn(resultSet, mappedName))
                    field.set(entity, resultSet.getInt(mappedName));
                break;
            case "boolean":
                if (isExistColumn(resultSet, mappedName))
                    field.set(entity, resultSet.getBoolean(mappedName));
                break;
            case "float":
                if (isExistColumn(resultSet, mappedName))
                    field.set(entity, resultSet.getFloat(mappedName));
                break;
            case "char":
                if (isExistColumn(resultSet, mappedName))
                    field.set(entity, resultSet.getByte(mappedName));
            default:
                field.set(entity, setEntity(ms, resultSet, field.getType().getName()));
        }
    }
}
