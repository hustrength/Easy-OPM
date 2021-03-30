package com.rsh.easy_opm.executor.resultset;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.config.ResultMapUnion;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.sqlsession.DefaultSession;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ResultSetHandler implements ResultHandler {
    MappedStatement ms;
    DefaultSession session;

    public abstract <E> List<E> handleResultSet(Object resultSet) throws Exception;

    @SuppressWarnings("unchecked")
    <E> List<E> executeCollection(List<E> resultList, ResultMapUnion union, String collectionId) {
        String property = union != null ? union.getUnionProperty() : null;

        // when id==null, the union node does not exist or only association exist
        if (collectionId == null)
            return resultList;

        // collection node exists, so unite results to one collection class
        Map<Object, E> unionMap = new HashMap<>();
        try {
            Class<E> entityClass = (Class<E>) Class.forName(ms.getResultType());
            Field collectionIdField = entityClass.getDeclaredField(collectionId);
            Field unitedField = entityClass.getDeclaredField(property);
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
                    if (source != null) {
                        AssertError.notFoundError(source.size() != 0, "Collection Class is null, so cannot unite Collection");
                        Object sourceObj = source.get(0);
                        if (sourceObj != null)
                            target.add(sourceObj);
                    }
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

    @SuppressWarnings("unchecked")
    <E> E multipleStepQuery(E formerQueryResult, ResultMapUnion union) {
        if (formerQueryResult == null)
            return null;
        String property = union.getUnionProperty();
        String select = union.getUnionSelect();
        String column = union.getUnionColumn();
        String ofType = union.getUnionOfType();

        try {
            Class<?> mapperInterface = session.getMapperInterface();
            Class<?> entityClass = formerQueryResult.getClass();
            Field nextStepQueryParam = null;
            try {
                nextStepQueryParam = entityClass.getDeclaredField(column);
                nextStepQueryParam.setAccessible(true);
            } catch (NoSuchFieldException e) {
                AssertError.notFoundError(false, "Union column attribute[" + column + ']',
                        "Field names of " + mapperInterface.getName());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // get next query method
            Method nextStepQueryMethod = mapperInterface.getDeclaredMethod(select, nextStepQueryParam.getType());
            nextStepQueryMethod.setAccessible(true);

            // check if next query return type is matched with designated type in union
            if (List.class.isAssignableFrom(nextStepQueryMethod.getReturnType())) {
                String nextReturnType = nextStepQueryMethod.getGenericReturnType().getTypeName();
                String regex = "<([^>]*)>";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(nextReturnType);
                if (m.find()) {
                    AssertError.notMatchedError(m.group(1).equals(ofType),
                            "The return type of next step query method", nextReturnType,
                            "designated return type in union", ofType);
                }
            } else {
                String nextReturnType = nextStepQueryMethod.getReturnType().getName();
                AssertError.notMatchedError(nextReturnType.equals(ofType),
                        "The return type of next step query method", nextReturnType,
                        "designated return type in union", ofType);
            }
            // the field that nextQueryResult will be assigned to
            Field propertyField = entityClass.getDeclaredField(property);
            propertyField.setAccessible(true);

            // execute next SQL query
            Object param = nextStepQueryParam.get(formerQueryResult);

            // when the ext query returns null, return former query result
            if (param == null) {
                return formerQueryResult;
            }

            Object nextQueryResult = null;
            if (param instanceof Number || param instanceof String || param instanceof Date
                    || param instanceof Boolean || param instanceof Character) {
                nextQueryResult = nextStepQueryMethod.invoke(session.getProxy(), param);
            } else
                AssertError.notSupported("The parameter of next query[" + nextStepQueryMethod.getName() + ']', param.getClass().getName());

            // if the propertyField Class is List
            if (List.class.isAssignableFrom(propertyField.getType())) {
                List<Object> collectionList = new ArrayList<>();

                // if the next query returns a List
                if (nextQueryResult instanceof List) {

                    // If the propertyField type equals to ofType, the union must be an association. Otherwise it is a collection.
                    // The next query returns a List, but the union is an association. So only fetch the 1st element of the List.
                    if (propertyField.getType().getName().equals(ofType)) {
                        collectionList.add(((List<Object>) nextQueryResult).get(0));
                        AssertError.warning("Next query returns a List, but the union is an association. So only fetch the 1st element of the List.");
                    } else {
                        collectionList.addAll((List<Object>) nextQueryResult);
                    }
                }
                // if the next query returns a single entity
                else {
                    collectionList.add(nextQueryResult);
                }
                propertyField.set(formerQueryResult, collectionList);
            }
            // if the propertyField Class is a single entity
            else {
                // The next query returns a List, but the field type is a single entity. So only fetch the 1st element of the List.
                if (nextQueryResult instanceof List) {
                    Object singleEntity = ((List<Object>) nextQueryResult).get(0);
                    AssertError.warning("Next query returns a List, but the type of the field[" + propertyField.getName() + "] is a single entity. So only fetch the 1st element of the List.");
                    propertyField.set(formerQueryResult, singleEntity);
                } else {
                    propertyField.set(formerQueryResult, nextQueryResult);
                }
            }
            return formerQueryResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
