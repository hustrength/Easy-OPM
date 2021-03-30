package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.*;
import com.rsh.easy_opm.error.AssertError;
import org.neo4j.driver.Driver;

import java.sql.Connection;

public class SessionFactory {
    public enum DB_TYPE {
        RD, GD
    }

    public DefaultSession getSession(DB_TYPE type) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        Configuration config = configBuilder.getConfig();
        switch (type) {
            case RD:
                Connection conn = new ConnectionBuilder(config).getRDConnection();
                return new DefaultSqlSession(config, conn);
            case GD:
                Driver driver = new ConnectionBuilder(config).getGDConnection();
                return new DefaultCqlSession(config, driver);
            default:
                AssertError.notSupported("DB_TYPE", type.toString());
                return null;
        }
    }
}
