package com.rsh.easy_opm.executor;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.executor.parameter.*;
import com.rsh.easy_opm.executor.resultset.*;
import com.rsh.easy_opm.executor.statement.*;
import com.rsh.easy_opm.sqlsession.SqlSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BaseExecutor implements Executor {
    Connection conn;
    SqlSession sqlSession;

    public BaseExecutor(Connection conn, SqlSession sqlSession) {
        this.conn = conn;
        this.sqlSession = sqlSession;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter, Class<E> mapperInterface) throws SQLException {
        // Instantiate ReplacedParameterHandler Class
        ParameterHandler replacedParamHandler = new ReplacedParameterHandler(ms.getSql());

        // Set replaced parameters
        String replacedSql = (String) replacedParamHandler.setParameters(ms.getParaType(), ms.getReplacedParamOrder(), parameter);
        ms.setSql(replacedSql);

        // Instantiate StatementHandler Class
        StatementHandler statementHandler = new DefaultStatementHandler(ms);

        // Get PreparedStatement from conn
        PreparedStatement preparedStatement = statementHandler.prepare(conn);

        // Instantiate PreparedParameterHandler Class
        ParameterHandler preparedParamHandler = new PreparedParameterHandler(preparedStatement);

        // Set prepared parameters
        preparedParamHandler.setParameters(ms.getParaType(), ms.getPreparedParamOrder(), parameter);

        // Execute SQL and get ResultSet
        ResultSet resultSet = statementHandler.execute(preparedStatement);

        // Instantiate ResultSetHandler Class and convert result to POJO
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(ms, sqlSession);

        // Get SQL result
        List<E> result = resultSetHandler.handleResultSet(resultSet);
        if (ms.getCommandType().equals("select") && result.size() == 0)
            System.out.println("The result of SELECT is null");
        return result;
    }
}
