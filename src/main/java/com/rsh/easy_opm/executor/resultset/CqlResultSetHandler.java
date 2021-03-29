package com.rsh.easy_opm.executor.resultset;


import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.reflection.GdReflectionUtil;
import com.rsh.easy_opm.reflection.ReflectionUtil;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CqlResultSetHandler implements ResultSetHandler{
    MappedStatement ms;

    public CqlResultSetHandler(MappedStatement ms) {
        this.ms = ms;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handleResultSet(Object result){
        if (result == null)
            return null;

        Result resultSet = (Result) result;

        String resultType = ms.getResultType();
        Map<String, String> resultMap = ms.getResultMap();

        ReflectionUtil reflectionUtil = new GdReflectionUtil(resultType, resultMap, resultSet);
        List<E> resultList = new ArrayList<>();

        while (resultSet.hasNext()) {
            E entityClass = (E) reflectionUtil.convertToBean();

            if (entityClass != null)
                resultList.add(entityClass);
        }
        return resultList;
    }
}
