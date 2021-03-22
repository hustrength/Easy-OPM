package com.rsh.easy_opm.reflection;

import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionUtil {
    private final String resultType;
    private final Map<String, String> resultMap;
    private final ResultSet resultSet;
    private final String unionOfType;

    public ReflectionUtil(String resultType, String unionOfType, Map<String, String> resultMap, ResultSet resultSet) {
        this.resultType = resultType;
        this.resultMap = resultMap;
        this.resultSet = resultSet;
        this.unionOfType = unionOfType;
    }

    @SuppressWarnings("unchecked")
    public <T> Object convertToBean() {
        try {
            Class<T> entityClass = (Class<T>) Class.forName(resultType);
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

    private String mapResult(String fieldName) {
        if (resultMap == null)
            return fieldName;
        return resultMap.getOrDefault(fieldName, fieldName);
    }

    private boolean existColumn(String columnName) {
        try {
            if (resultSet.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private <T> void setField(Field field, String mappedName, T entity) throws Exception {
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
                // when the field type is the child class of Collection, set Collection Class to the field
                if (List.class.isAssignableFrom(field.getType())) {
                    T entityValue = setEntity(unionOfType);
                    List<T> entityList = new ArrayList<>();
                    entityList.add(entityValue);
                    field.set(entity, entityList);
                } else {
                    T entityValue = setEntity(field.getType().getName());
                    field.set(entity, entityValue);
                }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T setEntity(String classType) {
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
