package com.rsh.easy_opm.executor;

import com.rsh.easy_opm.config.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {
    <E> List<E> query(MappedStatement ms, Object[] parameter, Class<E> mapperInterface) throws Exception;
}
