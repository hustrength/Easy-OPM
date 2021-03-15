package com.rsh.easy_opm.executor.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultSetHandler {
    <E> List<E> handleResultSet(ResultSet resultSet) throws SQLException;

}
