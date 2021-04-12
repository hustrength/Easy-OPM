package com.rsh.easy_opm.reflection;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RdReflectionUtil extends ReflectionUtil{

    public RdReflectionUtil(String resultType, String unionOfType, Map<String, String> resultMap) {
        this.resultType = resultType;
        this.resultMap = resultMap;
        this.unionOfType = unionOfType;
    }

    @SuppressWarnings("unchecked")
    <T> Object convertToEntityBean(Object result) throws Exception {
        if (iterateNumExceed(resultType))
            return null;

        boolean entityNotNull = false;

        Class<T> entityClass = (Class<T>) Class.forName(resultType);
        T entity = entityClass.getConstructor().newInstance();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String mappedName = mapResult(fieldName);
            if (setField(field, mappedName, entity, result))
                entityNotNull = true;
        }
        return entityNotNull ? entity : null;
    }

    Object convertToBasicBean(String type, Object result) throws SQLException{
        ResultSet resultSet = (ResultSet) result;
        switch (type){
            case "String":
                return resultSet.getString(0);
            case "Integer":
            case "int":
                return resultSet.getInt(0);
            case "Boolean":
            case "boolean":
                return resultSet.getBoolean(0);
            case "Float":
            case "float":
                return resultSet.getFloat(0);
            case "Character":
            case "char":
                return resultSet.getString(0).charAt(0);
            case "Byte":
            case "byte":
                return resultSet.getByte(0);
            case "Short":
            case "short":
                return resultSet.getShort(0);
            case "Long":
            case "long":
                return resultSet.getLong(0);
            case "Double":
            case "double":
                return resultSet.getDouble(0);
            case "Date":
                return resultSet.getDate(0);
            default:
                return null;
        }
    }

    boolean existColumn(String columnName, Object result) {
        ResultSet resultSet = (ResultSet) result;
        try {
            if (resultSet.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
    <T> boolean setField(Field field, String mappedName, T entity, Object result) throws Exception{
        ResultSet resultSet = (ResultSet) result;
        field.setAccessible(true);

        // do not set the field if not existing in resultSet
        String fieldType = field.getType().getSimpleName();
        boolean haveSet = false;
        switch (fieldType) {
            case "Date":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getDate(mappedName));
                }
                break;
            case "String":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getString(mappedName));
                }
                break;
            case "Integer":
            case "int":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getInt(mappedName));
                }
                break;
            case "Boolean":
            case "boolean":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getBoolean(mappedName));
                }
                break;
            case "Float":
            case "float":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getFloat(mappedName));
                }
                break;
            case "Character":
            case "char":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getString(mappedName).charAt(0));
                }
                break;
            case "Byte":
            case "byte":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getByte(mappedName));
                }
                break;
            case "Short":
            case "short":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getShort(mappedName));
                }
                break;
            case "Long":
            case "long":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getLong(mappedName));
                }
                break;
            case "Double":
            case "double":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    field.set(entity, resultSet.getDouble(mappedName));
                }
                break;
            default:
                if (unionOfType != null) {
                    // when the field type is the child class of Collection, set Collection Class to the field
                    if (List.class.isAssignableFrom(field.getType())) {
                        Object entityValue = setEntity(unionOfType, result);
                        List<Object> entityList = new ArrayList<>();
                        if (entityValue != null) {
                            haveSet = true;
                            entityList.add(entityValue);
                            field.set(entity, entityList);
                        }
                    } else {
                        T entityValue = setEntity(field.getType().getName(), result);
                        if (entityValue != null) {
                            haveSet = true;
                            field.set(entity, entityValue);
                        }
                    }
                }
        }
        return haveSet;
    }

    @SuppressWarnings("unchecked")
    <T> T setEntity(String classType, Object result) {
        boolean entityNotNull = false;
        try {
            if (iterateNumExceed(classType))
                return null;

            Class<T> entityClass = (Class<T>) Class.forName(classType);
            T entity = entityClass.getConstructor().newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName() + '@' + classType;
                String mappedName = mapResult(fieldName);
                if (setField(field, mappedName, entity, result))
                    entityNotNull = true;
            }
            return entityNotNull ? entity : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
