package com.rsh.easy_opm.executor.resultset;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.reflection.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetHandler implements ResultSetHandler {
    private MappedStatement mappedStatement;

    public DefaultResultSetHandler(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> handleResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null)
            return null;
        List<E> ret = new ArrayList<>();
        while (resultSet.next()) {
            E entityClass = (E) ReflectionUtil.convertToBean(mappedStatement, resultSet);
            if (entityClass != null)
                ret.add(entityClass);
        }
        return ret;
    }
}
