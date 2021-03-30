package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBuilder {
    Configuration config;

    public ConnectionBuilder(Configuration config) {
        this.config = config;
    }

    public Connection getRDConnection() {
        Connection conn = null;
        try {
            Class.forName(config.getDbDriver());
            conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUserName(), config.getDbPassword());
            conn.setAutoCommit(true);
        } catch (Exception e) {
            System.out.println("Fail to connect relational database");
            System.out.println(config);
            System.out.println();
        }
        return conn;
    }

    public Driver getGDConnection() {
        Driver driver = null;
        try {
            driver = GraphDatabase.driver(config.getDbUrl(),
                    AuthTokens.basic(config.getDbUserName(), config.getDbPassword()));
        }catch (Exception e){
            System.out.println("Fail to connect graph database");
            System.out.println(config);
            System.out.println();
        }
        return driver;
    }
}
