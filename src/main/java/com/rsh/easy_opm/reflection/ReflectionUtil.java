package com.rsh.easy_opm.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class ReflectionUtil implements Reflection {
    String resultType;
    Map<String, String> resultMap;
    String unionOfType;

    // Record the number of iterating of entities class
    Map<String, Integer> classIterateNum = new HashMap<>();
    // The max number of iterating of one entity class
    static int MAX_ITERATE_NUM = 1;

    abstract Object convertToBasicBean(String type, Object result) throws Exception;

    abstract boolean existColumn(String columnName, Object result);

    abstract <T> Object convertToEntityBean(Object result) throws Exception;

    abstract <T> boolean setField(Field field, String mappedName, T entity, Object result) throws Exception;

    abstract <T> T setEntity(String classType, Object result);

    public Object convertToBean(Object result) {
        try {
            // clear the records of entities iteration
            classIterateNum.clear();
            Object basicType = convertToBasicBean(resultType, result);
            if (basicType != null)
                return basicType;

            return convertToEntityBean(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String mapResult(String fieldName) {
        if (resultMap == null)
            return fieldName;
        return resultMap.getOrDefault(fieldName, fieldName);
    }

    boolean iterateNumExceed(String classType) {
        // get the number of iterating of this entity class
        int iterateNum = classIterateNum.getOrDefault(classType, 0);
        // when the iterateNum > 2, do not set this entity
        if (iterateNum > MAX_ITERATE_NUM)
            return true;
        // iterateNum++
        classIterateNum.put(classType, 1 + iterateNum);
        return false;
    }
}
