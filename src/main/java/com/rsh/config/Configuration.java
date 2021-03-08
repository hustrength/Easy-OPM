package com.rsh.config;

public class Configuration {
    /* members */
    private String dbUrl;

    private String dbUserName;

    private String dbPassword;

    private String dbDriver;

    public static final String CONFIG_FILE_PATH = "easy-opm.properties";

    public static final String MAPPER_FILE_PATH = "mapper";

    /* methods */
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
}
