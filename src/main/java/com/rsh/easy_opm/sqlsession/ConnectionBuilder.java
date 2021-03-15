package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBuilder {
    Configuration config;

    public ConnectionBuilder(Configuration config) {
        this.config = config;
    }

    public Connection getConnection() {
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
}
