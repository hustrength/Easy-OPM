package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.MappedStatement;

import java.util.List;

public interface SqlSession {
    //根据mapper接口获取接口对应的动态代理实现
    // get Mapper Interface provided by Binding Module
    <T> T getMapper(Class<T> type);

    <T> T selectOne(String sourceID, Object[] parameter);

    <E> List<E> selectList(String sourceID, Object[] parameter);

    Class<?> getMapperInterface();

    Object getProxy();

    void setProxy(Object proxy);

}
