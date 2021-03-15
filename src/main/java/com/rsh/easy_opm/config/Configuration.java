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

    /* constant */
    public static final String EASYOPM_CONFIG_PATH = "easy-opm.xml";

    // store mapper info
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // create dynamic proxy for mapper
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return MapperProxyFactory.getMapperProxy(sqlSession, type);
    }

    public MappedStatement queryMappedStatement(String sourceID){
        MappedStatement result = this.mappedStatements.get(sourceID);
        assert result !=null : "Source ID[" + sourceID + "] is invalid";
        return result;
    }

    public void checkConfig() {
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

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }
}
