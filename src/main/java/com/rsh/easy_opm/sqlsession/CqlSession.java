package com.rsh.easy_opm.sqlsession;

import java.util.List;

public interface CqlSession extends BasicSession{
    // get Mapper Interface provided by Binding Module
    <T> T getMapper(Class<T> type);

    <T> T selectOne(String sourceID, Object[] parameter);

    <E> List<E> selectList(String sourceID, Object[] parameter);

    void setProxy(Object proxy);
}
