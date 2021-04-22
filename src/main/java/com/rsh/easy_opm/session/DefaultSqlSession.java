package com.rsh.easy_opm.session;

import com.rsh.easy_opm.config.Configuration;
import com.rsh.easy_opm.executor.BaseSqlExecutor;

import java.sql.Connection;

public class DefaultSqlSession extends DefaultSession{
    public DefaultSqlSession(Configuration config, Connection conn) {
        this.config = config;
        this.executor = new BaseSqlExecutor(conn, this);
    }
}
