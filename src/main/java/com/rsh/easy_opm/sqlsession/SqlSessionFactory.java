package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.*;
import org.neo4j.driver.Driver;

import java.sql.Connection;

public class SqlSessionFactory {
    public enum DB_Type {
        RD, GD
    }

    public SqlSession getSession(DB_Type type) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        Configuration config = configBuilder.getConfig();
        switch (type) {
            case RD:
                Connection conn = new ConnectionBuilder(config).getRDConnection();
                return new DefaultSqlSession(config, conn);
            case GD:
                Driver driver = new ConnectionBuilder(config).getGDConnection();

        }
    }
}
