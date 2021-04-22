package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.session.DefaultSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    @SuppressWarnings("unchecked")
    public static <T> T getMapperProxy(DefaultSession session, Class<T> mapperInterface) {
        MapperProxy<T> mapperProxy = new MapperProxy<T>(session, mapperInterface);
        T proxy = (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
        mapperProxy.setProxyToSession(proxy);
        return proxy;
    }
}
