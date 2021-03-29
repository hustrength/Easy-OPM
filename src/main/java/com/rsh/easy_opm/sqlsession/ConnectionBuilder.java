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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Driver getGDConnection() {
        return GraphDatabase.driver(config.getDbDriver(),
                AuthTokens.basic(config.getDbUserName(), config.getDbPassword()));
    }
}
