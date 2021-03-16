package com.rsh.easy_opm.executor;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.executor.parameter.*;
import com.rsh.easy_opm.executor.resultset.*;
import com.rsh.easy_opm.executor.statement.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BaseExecutor implements Executor {
    Connection conn;

    public BaseExecutor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter) throws SQLException {
        // Instantiate ReplacedParameterHandler Class
        ReplacedParameterHandler replacedParamHandler = new ReplacedParameterHandler(ms.getSql());

        // Set replaced parameters
        replacedParamHandler.setParameters(ms.getParaType(), ms.getReplacedParamOrder(), parameter);

        // Instantiate StatementHandler Class
        ms.setSql(replacedParamHandler.getSql());
        StatementHandler statementHandler = new DefaultStatementHandler(ms);

        // Get PreparedStatement from conn
        PreparedStatement preparedStatement = statementHandler.prepare(conn);

        // Instantiate PreparedParameterHandler Class
        PreparedParameterHandler preparedParamHandler = new PreparedParameterHandler(preparedStatement );

        // Set prepared parameters
        preparedParamHandler.setParameters(ms.getParaType(), ms.getPreparedParamOrder(), parameter);

        // Execute SQL and get ResultSet
        ResultSet resultSet = statementHandler.execute(preparedStatement);

        // Instantiate ResultSetHandler Class and convert result to POJO
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(ms);

        // Get SQL result
        List<E> result = resultSetHandler.handleResultSet(resultSet);
        if (ms.getCommandType().equals("select") && result.size() == 0)
            System.out.println("The result of SELECT is null");
        return result;
    }
}
