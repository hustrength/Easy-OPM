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

    public ReflectionUtil(String resultType, Map<String, String> resultMap, ResultSet resultSet) {
        this.resultType = resultType;
        this.resultMap = resultMap;
        this.resultSet = resultSet;
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
        if (resultMap.containsKey(fieldName))
            return resultMap.get(fieldName);
        else return fieldName;
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
                    // get the generic type of List and retrieve the element type
                    String genericType = field.getGenericType().toString();
                    String pattern = "<([^>]*)>";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(genericType);

                    if (m.find()) {
                        String entityType = m.group(1);
                        T entityValue = setEntity(entityType);
                        List<T> entityList = new ArrayList<>();
                        entityList.add(entityValue);
                        field.set(entity, entityList);
                    } else AssertError.notMatchedError(false, genericType, "Regex\"" + pattern + '\"');
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
