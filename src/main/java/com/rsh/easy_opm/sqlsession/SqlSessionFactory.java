package com.rsh.easy_opm.sqlsession;

import com.rsh.easy_opm.config.*;

import java.sql.Connection;

public class SqlSessionFactory {
    public SqlSession getSession(){
        ConfigBuilder configBuilder = new ConfigBuilder();
        Configuration config = configBuilder.getConfig();
        Connection conn = new ConnectionBuilder(config).getConnection();
        return new DefaultSqlSession(config, conn);
    }
}
