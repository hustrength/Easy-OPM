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

    public void checkConfig() {
        assert dbSourceID != null : "Fail to get dbSourceID";
        assert dbConnType != null : "Fail to get dbConnType";
        assert dbDriver != null : "Fail to get dbDriver";
        assert dbUrl != null : "Fail to get dbUrl";
        assert dbUserName != null : "Fail to get dbUserName";
        assert dbPassword != null : "Fail to get dbPassword";
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "dbSourceID='" + dbSourceID + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbUserName='" + dbUserName + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbDriver='" + dbDriver + '\'' +
                ", dbConnType='" + dbConnType + '\'' +
                '}';
    }
}
