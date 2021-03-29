package com.rsh.easy_opm.reflection;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.types.Node;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GdReflectionUtil implements ReflectionUtil {
    private final String resultType;
    private final Map<String, String> resultMap;
    private final Result resultSet;
    private Record record = null;

    public GdReflectionUtil(String resultType, Map<String, String> resultMap, Result resultSet) {
        this.resultType = resultType;
        this.resultMap = resultMap;
        this.resultSet = resultSet;
    }
    @SuppressWarnings("unchecked")
    public Object convertToBean() {
        record = resultSet.next();
        try {
            Object basicType = convertToBasicBean(resultType);
            if (basicType != null)
                return basicType;

            return convertToEntityBean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Object convertToEntityBean()throws Exception{
        boolean entityNotNull = false;
        Class<T> entityClass = (Class<T>) Class.forName(resultType);
        T entity = (T) entityClass.newInstance();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String mappedName = mapResult(fieldName);
            if (setField(field, mappedName, entity))
                entityNotNull = true;
        }
        return entityNotNull ? entity : null;
    }

    private Object convertToBasicBean(String type) throws SQLException {
        switch (type){
            case "String":
            case "Date":
                return record.get(0).asString();
            case "Integer":
            case "int":
                return record.get(0).asInt();
            case "Boolean":
            case "boolean":
                return record.get(0).asBoolean();
            case "Float":
            case "float":
                return record.get(0).asFloat();
            case "Character":
            case "char":
                return record.get(0).asString().charAt(0);
            case "Byte":
            case "byte":
            case "Short":
            case "short":
                return record.get(0).asNumber();
            case "Long":
            case "long":
                return record.get(0).asLong();
            case "Double":
            case "double":
                return record.get(0).asDouble();
            default:
                return null;
        }
    }

    private String mapResult(String fieldName) {
        if (resultMap == null)
            return fieldName;
        return resultMap.getOrDefault(fieldName, fieldName);
    }

    private boolean existColumn(String columnName) {
        return record.containsKey(columnName);
    }

    private <T> boolean setField(Field field, String mappedName, T entity) throws Exception {
        field.setAccessible(true);

        // do not set the field if not existing in resultSet
        String fieldType = field.getType().getSimpleName();
        boolean haveSet = false;
        switch (fieldType) {
            case "Date":
            case "String":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asString());
                }
                break;
            case "Integer":
            case "int":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asInt());
                }
                break;
            case "Boolean":
            case "boolean":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asBoolean());
                }
                break;
            case "Float":
            case "float":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asFloat());
                }
                break;
            case "Character":
            case "char":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asString().charAt(0));
                }
                break;
            case "Byte":
            case "byte":
            case "Short":
            case "short":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asNumber());
                }
                break;
            case "Long":
            case "long":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asLong());
                }
                break;
            case "Double":
            case "double":
                if (existColumn(mappedName)) {
                    haveSet = true;
                    field.set(entity, record.get(mappedName).asDouble());
                }
                break;
            default:
                // when the field type is the child class of Collection, set Collection Class to the field
                if (List.class.isAssignableFrom(field.getType())) {
                    String genericType = field.getGenericType().getTypeName();
                    String regex = "<([^>]*)>";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(genericType);

                    String elementType;
                    if (m.find()) {
                        elementType = m.group(1);
                        Object entityValue = setEntity(elementType);
                        List<Object> entityList = new ArrayList<>();
                        if (entityValue != null) {
                            haveSet = true;
                            entityList.add(entityValue);
                            field.set(entity, entityList);
                        }
                    }
                } else {
                    T entityValue = setEntity(field.getType().getName());
                    if (entityValue != null) {
                        haveSet = true;
                        field.set(entity, entityValue);
                    }
                }
        }
        return haveSet;
    }

    @SuppressWarnings("unchecked")
    private <T> T setEntity(String classType) {
        boolean entityNotNull = false;
        try {
            Class<T> entityClass = (Class<T>) Class.forName(classType);
            T entity = (T) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName() + '@' + classType;
                String mappedName = mapResult(fieldName);
                if (setField(field, mappedName, entity))
                    entityNotNull = true;
            }
            return entityNotNull ? entity : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
