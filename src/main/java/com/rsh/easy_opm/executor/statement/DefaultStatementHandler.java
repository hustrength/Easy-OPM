package com.rsh.easy_opm.executor.statement;

import com.rsh.easy_opm.config.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultStatementHandler implements StatementHandler{
    private MappedStatement mappedStatment;

    public DefaultStatementHandler(MappedStatement mappedStatment) {
        this.mappedStatment = mappedStatment;
    }

    @Override
    public PreparedStatement prepare(Connection connection) throws SQLException {
        return connection.prepareStatement(mappedStatment.getSql());
    }

    @Override
    public ResultSet execute(PreparedStatement statement) {
        try {
            switch (mappedStatment.getCommandType()){
                case "select":
                    return statement.executeQuery();
                case "update":
                case "insert":
                case "delete":
                    statement.executeUpdate();
                    return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
