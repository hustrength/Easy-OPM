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

        //如果是Object本身的方法不增强
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        //获取方法的返回参数class对象
        Class<T> returnType = (Class<T>) method.getReturnType();
        Object ret = null;

        //根据不同的返回参数类型调用不同的SqlSession不同的方法
        if (isCollection(returnType)) {
//            ret = sqlSession.selectList(mapperInterface.getName() + "." + method.getName(), args);
        } else {
//            ret = sqlSession.selectOne(mapperInterface.getName() + "." + method.getName(), args);
        }
        return ret;
    }

    private boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
