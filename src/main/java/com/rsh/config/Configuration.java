package com.rsh.config;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private String dbUrl;

    private String dbUserName;

    private String dbPassword;

    private String dbDriver;

    private final Map<String, MapperStatement> mapperStatements = new HashMap<>();

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

    public Map<String, MapperStatement> getMapperStatements() {
        return mapperStatements;
    }
}
