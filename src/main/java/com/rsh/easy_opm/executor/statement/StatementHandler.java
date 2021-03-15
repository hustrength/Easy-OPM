package com.rsh.easy_opm.executor.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface StatementHandler {
    // get statement from a connection
    PreparedStatement prepare(Connection connection) throws SQLException;

    // execute SQL
    ResultSet query(PreparedStatement statement);
}
