package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.sqlsession.BasicSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    @SuppressWarnings("unchecked")
    public static <T> T getMapperProxy(BasicSession sqlSession, Class<T> mapperInterface) {
        MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface);
        T proxy = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
        mapperProxy.setProxyToSession(proxy);
        return proxy;
    }
}
