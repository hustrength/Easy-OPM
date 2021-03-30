package com.rsh.easy_opm.reflection;

import com.rsh.easy_opm.error.AssertError;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GdReflectionUtil extends ReflectionUtil {

    public GdReflectionUtil(String resultType, String unionOfType, Map<String, String> resultMap) {
        this.resultType = resultType;
        this.resultMap = resultMap;
        this.unionOfType = unionOfType;
    }

    @SuppressWarnings("unchecked")
    <T> Object convertToEntityBean(Object result) throws Exception {
        if (iterateNumExceed(resultType))
            return null;

        boolean entityNotNull = false;

        Class<T> javaClass = (Class<T>) Class.forName(resultType);
        T javaEntity = javaClass.getConstructor().newInstance();

        // get the graph database entity, such as Node, Relationship, etc.
        String thisColumn = mapResult("this");
        Entity gdEntity = null;
        if (!thisColumn.equals("this"))
            gdEntity = ((Record) result).get(thisColumn).asEntity();

        Field[] declaredFields = javaClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String mappedName = mapResult(fieldName);

            if (mappedName.charAt(0) == '@') {
                AssertError.notFoundError(gdEntity != null, "The mapping of the entity itself", "resultMap");
                if (setField(field, mappedName.substring(1), javaEntity, gdEntity))
                    entityNotNull = true;
            } else {
                if (setField(field, mappedName, javaEntity, result))
                    entityNotNull = true;
            }
        }
        return entityNotNull ? javaEntity : null;
    }

    Object convertToBasicBean(String type, Object result) {
        Record record = (Record) result;
        switch (type) {
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

    boolean existColumn(String columnName, Object result) {
        if (result instanceof Record) {
            Record record = (Record) result;
            return record.containsKey(columnName);
        } else if (result instanceof Entity) {
            Entity entity = (Entity) result;
            return entity.containsKey(columnName);
        } else return false;
    }

    <T> boolean setField(Field field, String mappedName, T entity, Object result) throws Exception {
        boolean isRecord = false;
        if (result instanceof Record) {
            isRecord = true;
        }

        field.setAccessible(true);

        // do not set the field if not existing in resultSet
        String fieldType = field.getType().getSimpleName();
        boolean haveSet = false;
        switch (fieldType) {
            case "Date":
            case "String":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asString());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asString());
                }
                break;
            case "Integer":
            case "int":
                // for symbol "$id", set the identity of entity
                if (mappedName.equals("$id")){
                    haveSet = true;
                    field.set(entity, (int)((Entity) result).id());
                    break;
                }

                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asInt());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asInt());
                }
                break;
            case "Boolean":
            case "boolean":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asBoolean());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asBoolean());
                }
                break;
            case "Float":
            case "float":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asFloat());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asFloat());
                }
                break;
            case "Character":
            case "char":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asString().charAt(0));
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asString().charAt(0));
                }
                break;
            case "Byte":
            case "byte":
            case "Short":
            case "short":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asNumber());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asNumber());
                }
                break;
            case "Long":
            case "long":
                // for symbol "$id", set the identity of entity
                if (mappedName.equals("$id")){
                    haveSet = true;
                    field.set(entity, ((Entity) result).id());
                    break;
                }

                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asLong());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asLong());
                }
                break;
            case "Double":
            case "double":
                if (existColumn(mappedName, result)) {
                    haveSet = true;
                    if (isRecord)
                        field.set(entity, ((Record) result).get(mappedName).asDouble());
                    else
                        field.set(entity, ((Entity) result).get(mappedName).asDouble());
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
                        Object entityValue = setEntity(elementType, result);
                        List<Object> entityList = new ArrayList<>();
                        if (entityValue != null) {
                            haveSet = true;
                            entityList.add(entityValue);
                            field.set(entity, entityList);
                        }
                    }
                } else {
                    T entityValue = setEntity(field.getType().getName(), result);
                    if (entityValue != null) {
                        haveSet = true;
                        field.set(entity, entityValue);
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

            // get the graph database entity, such as Node, Relationship, etc.
            String thisColumn = "this@" + classType;
            String mappedThisColumn = mapResult(thisColumn);
            // when fieldName is the same as mappedName, this property mapping is not set
            if (thisColumn.equals(mappedThisColumn))
                return null;

            Entity gdEntity = ((Record) result).get(mappedThisColumn).asEntity();

            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName() + '@' + classType;
                String mappedName = mapResult(fieldName);

                // when fieldName is the same as mappedName, this property mapping is not set
                if (!fieldName.equals(mappedName)) {
                    if (setField(field, mappedName, entity, gdEntity))
                        entityNotNull = true;
                }
            }
            return entityNotNull ? entity : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
