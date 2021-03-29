package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.annotation.*;
import com.rsh.easy_opm.config.AnnotationParser;
import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.sqlsession.DefaultSqlSession;
import com.rsh.easy_opm.sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collection;

public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    private final Configuration config;
    private final AnnotationParser annotationParser;

    MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.config = ((DefaultSqlSession) sqlSession).getConfig();
        this.annotationParser = new AnnotationParser(mapperInterface);
    }

    public void setProxyToSqlSession(T proxy) {
        sqlSession.setProxy(proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // do not enhance the method if this is Object Class
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        Class<?> returnType = method.getReturnType();
        Object ret;

        // invoke different methods in SqlSession according to different return types
        String sourceID = mapperInterface.getName() + "." + method.getName();

        // if using annotation to set mapper, generate MappedStatement from annotation
        if (existAnnotationSetting(sourceID, method)) {
            MappedStatement ms = annotationParser.parse(method);

            AssertError.notFoundError(ms != null, "XML or Annotation Mapper settings both");
            config.getMappedStatements().put(sourceID, ms);
        }

        if (isCollection(returnType)) {
            ret = sqlSession.selectList(sourceID, args);
        } else {
            ret = sqlSession.selectOne(sourceID, args);
        }

        return ret;
    }

    private boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    private boolean existAnnotationSetting(String sourceID, Method method) {
        MappedStatement ms = config.queryMappedStatement(sourceID);
        if (ms == null) {
            // when XML Setting is not used, check if defining @Mapper
            Class<?> entityType = method.getDeclaringClass();
            AssertError.notFoundError(entityType.isAnnotationPresent(Mapper.class), "Annotation @Mapper");

            return true;
        } else {
            AssertError.warning("For Method[" + method.toString() + "],\n\t\t" + "XML and Annotation Mapper are both set. Use XML Mapper in priority");
            return false;
        }
    }
}
