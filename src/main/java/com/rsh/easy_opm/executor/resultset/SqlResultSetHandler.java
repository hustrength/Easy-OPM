package com.rsh.easy_opm.executor.resultset;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.config.ResultMapUnion;
import com.rsh.easy_opm.reflection.RdReflectionUtil;
import com.rsh.easy_opm.reflection.ReflectionUtil;
import com.rsh.easy_opm.session.DefaultSqlSession;

import java.sql.ResultSet;
import java.util.*;

public class SqlResultSetHandler extends ResultSetHandler {
    public SqlResultSetHandler(MappedStatement ms, DefaultSqlSession session) {
        this.ms = ms;
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> handleResultSet(Object result) throws Exception {
        if (result == null)
            return null;

        ResultSet resultSet = (ResultSet) result;

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
        ReflectionUtil reflectionUtil = new RdReflectionUtil(resultType, unionOfType, resultMap);

        List<E> resultList = new ArrayList<>();
        while (resultSet.next()) {
            E entityClass = (E) reflectionUtil.convertToBean(resultSet);

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
