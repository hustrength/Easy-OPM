package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.executor.Executor;

import java.util.List;

public abstract class DefaultSession implements BasicSession{
    Configuration config;

    Executor executor;
    Class<?> mapperInterface;
    Object proxy;

    public <T> T getMapper(Class<T> type) {
        mapperInterface = type;
        return config.getMapper(type, this);
    }

    public <T> T selectOne(String sourceID, Object[] parameter) {
        List<Object> selectList = this.selectList(sourceID, parameter);
        if (selectList != null && selectList.size() > 0) {
            return (T) selectList.get(0);
        }
        return null;
    }

    public <E> List<E> selectList(String sourceID, Object[] parameter) {
        MappedStatement mappedStatement = config.queryMappedStatement(sourceID);
        AssertError.notMatchedError(mappedStatement != null, "Mapper source id", sourceID);
        try {
            return executor.query(mappedStatement, parameter, (Class<E>) mapperInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}