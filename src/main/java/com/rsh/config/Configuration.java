package com.rsh.config;

public class Configuration {
    /* variables */
    private String dbSourceID;

    private String dbUrl;

    private String dbUserName;

    private String dbPassword;

    private String dbDriver;

    private String dbConnType;

    /* constant */
    public static final String CONFIG_FILE_PATH = "easy-opm.properties";

    public static final String MAPPER_FILE_PATH = "mapper";

    public static final String EASYOPM_CONFIG_PATH = "easy-opm.xml";

    /* methods */

    public String getDbSourceID() {
        return dbSourceID;
    }

    public void setDbSourceID(String dbSourceID) {
        this.dbSourceID = dbSourceID;
    }

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

    public void checkConfig(){
        assert !dbSourceID.isEmpty();
        assert !dbConnType.isEmpty();
        assert !dbDriver.isEmpty();
        assert !dbUrl.isEmpty();
        assert !dbUserName.isEmpty();
        assert !dbPassword.isEmpty();
    }
}
