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

public class BaseExecutor implements Executor{
    Connection conn;

    public BaseExecutor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter) throws SQLException {
        // Instantiate StatementHandler Class and get PreparedStatement from conn
        StatementHandler statementHandler = new DefaultStatementHandler(ms);
        PreparedStatement preparedStatement = statementHandler.prepare(conn);

        // Instantiate ParameterHandler Class and set parameters
        ParameterHandler parameterHandler = new DefaultParameterHandler(ms.getParaType(), ms.getParaOrder(), parameter);
        parameterHandler.setParameters(preparedStatement);

        // Execute SQL and get ResultSet
        ResultSet resultSet = statementHandler.execute(preparedStatement);

        // Instantiate ResultSetHandler Class and convert result to POJO
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(ms);

        return resultSetHandler.handleResultSet(resultSet);
    }
}
