package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;
import com.rsh.easy_opm.executor.BaseExecutor;
import com.rsh.easy_opm.executor.Executor;
import org.neo4j.driver.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession{
    private Configuration config;

    private Connection conn;
    private Driver driver;
    private Executor executor;
    private Class<?> mapperInterface;
    private Object proxy;


    public DefaultSqlSession(Configuration config, Connection conn) {
        this.config = config;
        this.conn = conn;
        this.executor = new BaseExecutor(this.conn, this);
    }

    public DefaultSqlSession(Configuration config, Driver driver) {
        this.config = config;
        this.driver = driver;
//        this.executor = new BaseExecutor(this.driver, this);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    @Override
    public Object getProxy() {
        return proxy;
    }

    @Override
    public void setProxy(Object proxy) {
        this.proxy = proxy;
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

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
