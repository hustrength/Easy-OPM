package com.rsh.easy_opm.executor.resultset;

import java.util.List;

public interface ResultHandler {
    <E> List<E> handleResultSet(Object resultSet) throws Exception;
}
