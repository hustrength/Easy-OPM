package com.rsh.easy_opm.binding;

import com.rsh.easy_opm.annotation.*;
import com.rsh.easy_opm.config.AnnotationParser;
import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.sqlsession.DefaultSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collection;

public class MapperProxy<T> implements InvocationHandler {

    private final DefaultSession session;
    private final Class<T> mapperInterface;
    private final Configuration config;
    private final AnnotationParser annotationParser;

    MapperProxy(DefaultSession session, Class<T> mapperInterface) {
        this.session = session;
        this.mapperInterface = mapperInterface;
        this.config = session.getConfig();
        this.annotationParser = new AnnotationParser(mapperInterface);
    }

    public void setProxyToSession(T proxy) {
        session.setProxy(proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){

        // do not enhance the method if this is Object Class
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Class<?> returnType = method.getReturnType();
        Object ret = null;

        // invoke different methods in BasicSession according to different return types
        String sourceID = mapperInterface.getName() + "." + method.getName();

        // if using annotation to set mapper, generate MappedStatement from annotation
        if (existAnnotationSetting(sourceID, method)) {
            MappedStatement ms = annotationParser.parse(method);

            config.getMappedStatements().put(sourceID, ms);
        }
        try {
            if (isCollection(returnType)) {
                ret = session.selectList(sourceID, args);
            } else {
                ret = session.selectOne(sourceID, args);
            }
        }catch (Exception e){
            System.out.println("Fail to execute " + method.getName());
            if (isBoolean(returnType))
                ret = false;
            e.printStackTrace();
        }
        if (isBoolean(returnType))
            ret = true;
        return ret;
    }

    private boolean isBoolean(Class<?> type) {
        return Boolean.class.isAssignableFrom(type);
    }

    private boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    private boolean existAnnotationSetting(String sourceID, Method method) {
        MappedStatement ms = config.queryMappedStatement(sourceID);

        boolean existQuery = method.isAnnotationPresent(Select.class) || method.isAnnotationPresent(Update.class)
                || method.isAnnotationPresent(Insert.class) || method.isAnnotationPresent(Delete.class);

        if (ms == null) {
            AssertError.notFoundError(existQuery, "XML or Annotation Mapper settings both", method.toString());
            return true;
        } else {
            if (existQuery)
                AssertError.warning("For Method[" + method.toString() + "],\n\t\t" + "XML and Annotation Mapper are both set. Use XML Mapper in priority");
            return false;
        }
    }
}
