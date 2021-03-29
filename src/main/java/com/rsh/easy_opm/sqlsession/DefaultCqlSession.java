package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.executor.BaseCqlExecutor;
import com.rsh.easy_opm.executor.Executor;
import org.neo4j.driver.Driver;

import java.sql.SQLException;
import java.util.List;

public class DefaultCqlSession implements CqlSession{
    private Configuration config;

    private Executor executor;
    private Class<?> mapperInterface;
    private Object proxy;


    public DefaultCqlSession(Configuration config, Driver driver) {
        this.config = config;
        this.executor = new BaseCqlExecutor(driver, this);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        mapperInterface = type;
        return config.getMapper(type, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T selectOne(String sourceID, Object[] parameter) {
        List<Object> selectList = this.selectList(sourceID, parameter);
        if (selectList != null && selectList.size() > 0) {
            return (T) selectList.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
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

    @Override
    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}
