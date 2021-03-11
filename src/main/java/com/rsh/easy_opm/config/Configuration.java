package com.rsh.easy_opm.config;

import com.rsh.easy_opm.binding.MapperProxyFactory;
import com.rsh.easy_opm.sqlsession.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    /* variables */
    private String dbUrl;

    private String dbUserName;

    private String dbPassword;

    private String dbDriver;

    private String dbConnType;

    /* constant */
    public static final String EASYOPM_CONFIG_PATH = "easy-opm.xml";

    // store mapper info
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // create dynamic proxy for mapper
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return MapperProxyFactory.getMapperProxy(sqlSession, type);
    }

    public void checkConfig() {
        assert dbConnType != null : "Fail to get dbConnType";
        assert dbDriver != null : "Fail to get dbDriver";
        assert dbUrl != null : "Fail to get dbUrl";
        assert dbUserName != null : "Fail to get dbUserName";
        assert dbPassword != null : "Fail to get dbPassword";
    }

    @Override
    public String toString() {
        return "Configuration{" +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbUserName='" + dbUserName + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbDriver='" + dbDriver + '\'' +
                ", dbConnType='" + dbConnType + '\'' +
                '}';
    }

    /* getter and setter methods */
    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbConnType() {
        return dbConnType;
    }

    public void setDbConnType(String dbConnType) {
        this.dbConnType = dbConnType;
    }

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }
}
