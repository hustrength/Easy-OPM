package com.rsh.easy_opm.executor.resultset;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.reflection.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DefaultResultSetHandler implements ResultSetHandler {
    private final MappedStatement ms;

    public DefaultResultSetHandler(MappedStatement ms) {
        this.ms = ms;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handleResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null)
            return null;
//        System.out.println(ms.toString());
        List<E> resultList = new ArrayList<>();
        ReflectionUtil reflectionUtil = new ReflectionUtil(ms.getResultType(), ms.getResultMap(), resultSet);
        while (resultSet.next()) {
            E entityClass = (E) reflectionUtil.convertToBean();
            if (entityClass != null)
                resultList.add(entityClass);
        }
        return uniteCollectionClass(resultList);
    }

    @SuppressWarnings("unchecked")
    private <E> List<E> uniteCollectionClass(List<E> resultList) {
        String collectionId = ms.getCollectionId();
        String collectionProperty = ms.getCollectionProperty();
        if (collectionId == null || collectionProperty == null)
            return resultList;

        Map<Object, E> unionMap = new HashMap<>();
        try {
            Class<E> entityClass = (Class<E>) Class.forName(ms.getResultType());
            Field collectionIdField = entityClass.getDeclaredField(collectionId);
            Field unitedField = entityClass.getDeclaredField(collectionProperty);
            collectionIdField.setAccessible(true);
            unitedField.setAccessible(true);

            for (E entity :
                    resultList) {
                Object idValue = collectionIdField.get(entity);
                if (!unionMap.containsKey(idValue)) {
                    unionMap.put(idValue, entity);
                } else {
                    List source = (List) unitedField.get(entity);
                    List target = (List) unitedField.get(unionMap.get(idValue));
                    target.add(source.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Object> keySet = unionMap.keySet();
        resultList.clear();
        for (Object key :
                keySet) {
            resultList.add(unionMap.get(key));
        }
        return resultList;
    }
}
