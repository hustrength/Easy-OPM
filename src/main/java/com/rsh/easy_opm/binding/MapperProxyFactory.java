package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.sqlsession.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    @SuppressWarnings("unchecked")
    public static <T> T getMapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface);
        T proxy = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
        mapperProxy.setProxy(proxy);
        return proxy;
    }
}
