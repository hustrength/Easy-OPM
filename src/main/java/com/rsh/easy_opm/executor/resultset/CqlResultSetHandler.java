package com.rsh.easy_opm.executor.resultset;


import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.config.ResultMapUnion;
import com.rsh.easy_opm.reflection.GdReflectionUtil;
import com.rsh.easy_opm.reflection.ReflectionUtil;
import com.rsh.easy_opm.sqlsession.DefaultCqlSession;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CqlResultSetHandler extends ResultSetHandler {

    public CqlResultSetHandler(MappedStatement ms, DefaultCqlSession session) {
        this.ms = ms;
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handleResultSet(Object result) {
        if (result == null)
            return null;

        Result resultSet = (Result) result;

        String resultType = ms.getResultType();

        List<ResultMapUnion> unions = ms.getResultMapUnionList();
        String unionOfType = null;
        ResultMapUnion collection = null;

        // If the 1st union exists, it must be the collection node
        if (unions != null) {
            collection = unions.get(0);
            unionOfType = collection.getUnionOfType();
        }

        Map<String, String> resultMap = ms.getResultMap();
        ReflectionUtil reflectionUtil = new GdReflectionUtil(resultType, unionOfType, resultMap);

        List<E> resultList = new ArrayList<>();
        while (resultSet.hasNext()) {
            E entityClass = (E) reflectionUtil.convertToBean(resultSet.next());

            if (unions != null) {
                for (ResultMapUnion union :
                        unions) {
                    // conduct multiple steps query
                    if (union.getUnionSelect() != null)
                        entityClass = multipleStepQuery(entityClass, union);
                }
            }

            if (entityClass != null)
                resultList.add(entityClass);
        }
        return executeCollection(resultList, collection, ms.getCollectionId());
    }
}
