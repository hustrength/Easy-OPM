package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.executor.BaseExecutor;
import com.rsh.easy_opm.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession{
    private Configuration config;

    private Connection conn;
    private Executor executor;


    public DefaultSqlSession(Configuration config, Connection conn) {
        this.config = config;
        this.conn = conn;
        this.executor = new BaseExecutor(this.conn);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
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

    @Override
    public <E> List<E> selectList(String sourceID, Object[] parameter) {
        MappedStatement mappedStatement = config.queryMappedStatement(sourceID);
        try {
            return executor.query(mappedStatement, parameter);
        } catch (SQLException e) {
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

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
