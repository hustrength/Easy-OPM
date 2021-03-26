package com.rsh.easy_opm.executor.resultset;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.config.ResultMapUnion;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.reflection.ReflectionUtil;
import com.rsh.easy_opm.sqlsession.SqlSession;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultResultSetHandler implements ResultSetHandler {
    private final MappedStatement ms;
    private final SqlSession sqlSession;

    public DefaultResultSetHandler(MappedStatement ms, SqlSession sqlSession) {
        this.ms = ms;
        this.sqlSession = sqlSession;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handleResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null)
            return null;
        String resultType = ms.getResultType();

        ResultMapUnion union = ms.getResultMapUnion();
        String select = union != null ? union.getUnionSelect() : null;
        String column = union != null ? union.getUnionColumn() : null;
        String property = union != null ? union.getUnionProperty() : null;
        String unionOfType = union != null ? ms.getResultMapUnion().getUnionOfType() : null;

        Map<String, String> resultMap = ms.getResultMap();
        ReflectionUtil reflectionUtil = new ReflectionUtil(resultType, unionOfType, resultMap, resultSet);

        List<E> resultList = new ArrayList<>();
        while (resultSet.next()) {
            E entityClass = (E) reflectionUtil.convertToBean();

            // conduct multiple steps query
            if (select != null && column != null && property != null)
                entityClass = multipleStepQuery(entityClass, union);

            if (entityClass != null)
                resultList.add(entityClass);
        }
        return executeUnion(resultList, union);
    }

    @SuppressWarnings("unchecked")
    private <E> List<E> executeUnion(List<E> resultList, ResultMapUnion union) {
        String id = union != null ? union.getCollectionId() : null;
        String property = union != null ? union.getUnionProperty() : null;

        // when id==null, the union node does not exist or only association exist
        if (id == null)
            return resultList;

        // collection node exists, so unite results to one collection class
        Map<Object, E> unionMap = new HashMap<>();
        try {
            Class<E> entityClass = (Class<E>) Class.forName(ms.getResultType());
            Field collectionIdField = entityClass.getDeclaredField(id);
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
    private <E> E multipleStepQuery(E formerQueryResult, ResultMapUnion union) {
        if (formerQueryResult == null)
            return null;
        String id = union.getCollectionId();
        String property = union.getUnionProperty();
        String select = union.getUnionSelect();
        String column = union.getUnionColumn();
        String ofType = union.getUnionOfType();

        try {
            Class<?> mapperInterface = sqlSession.getMapperInterface();
            Class<?> entityClass = formerQueryResult.getClass();
            Field nextStepQueryParam = null;
            try {
                nextStepQueryParam = entityClass.getDeclaredField(column);
                nextStepQueryParam.setAccessible(true);
            } catch (NoSuchFieldException e) {
                AssertError.notFoundError(false, "Union column attribute[" + column + ']', "Field names of " + mapperInterface.getName());
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
                    AssertError.notMatchedError(m.group(1).equals(ofType), "The return type of next step query method", nextReturnType, "designated return type in union", ofType);
                }
            } else {
                String nextReturnType = nextStepQueryMethod.getReturnType().getName();
                AssertError.notMatchedError(nextReturnType.equals(ofType), "The return type of next step query method", nextReturnType, "designated return type in union", ofType);
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
            if (param instanceof Number || param instanceof String) {
                nextQueryResult = nextStepQueryMethod.invoke(sqlSession.getProxy(), param);
            } else
                AssertError.notSupported("The parameter of next query[" + nextStepQueryMethod.getName() + ']', param.getClass().getName());

            // If the propertyField type equals to ofType, the union is association. Otherwise it is collection.
            if (propertyField.getType().getName().equals(ofType)) {
                propertyField.set(formerQueryResult, nextQueryResult);
            }// if the union is collection and the propertyField Class is List
            else if (List.class.isAssignableFrom(propertyField.getType())) {
                if (nextQueryResult instanceof List) {
                    List collectionList = (List) propertyField.get(formerQueryResult);
                    collectionList.clear();

                    // The next query returns a List, but the union is an association. So only fetch the 1st element of the List.
                    if (id == null) {
                        collectionList.add(((List<Object>) nextQueryResult).get(0));
                        AssertError.warning("Next query returns a List, but the union is an association. So only fetch the 1st element of the List.");
                    } else {
                        collectionList.addAll((List<Object>) nextQueryResult);
                    }
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
