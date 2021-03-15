package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collection;

public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;

    MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // do not enhance the method if this is Object Class
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        Class<T> returnType = (Class<T>) method.getReturnType();
        Object ret = null;

        // invoke different methods in SqlSession according to different return types
        String sourceID = mapperInterface.getName() + "." + method.getName();
        if (isCollection(returnType)) {
            ret = sqlSession.selectList(sourceID, args);
        } else {
            ret = sqlSession.selectOne(sourceID, args);
        }
        return ret;
    }

    private boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
