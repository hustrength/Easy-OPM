package com.rsh.easy_opm.executor.sqlstatement;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.error.AssertError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultStatementHandler implements StatementHandler {
    private final MappedStatement mappedStatment;

    public DefaultStatementHandler(MappedStatement mappedStatment) {
        this.mappedStatment = mappedStatment;
    }

    @Override
    public PreparedStatement prepare(Connection connection) throws SQLException {
        return connection.prepareStatement(mappedStatment.getQueryStr());
    }

    @Override
    public ResultSet execute(PreparedStatement statement) throws SQLException {
        String commandType = mappedStatment.getCommandType();
        switch (commandType) {
            case "select":
                return statement.executeQuery();
            case "update":
            case "insert":
            case "delete":
                statement.executeUpdate();
                return null;
            default:
                AssertError.notSupported("Operation", commandType);
        }
        return null;
    }
}
