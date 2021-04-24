package com.rsh.easy_opm.executor;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.executor.parameter.*;
import com.rsh.easy_opm.executor.resultset.*;
import com.rsh.easy_opm.executor.sqlstatement.*;
import com.rsh.easy_opm.session.DefaultSqlSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class BaseSqlExecutor implements Executor {
    Connection conn;
    DefaultSqlSession sqlSession;

    public BaseSqlExecutor(Connection conn, DefaultSqlSession sqlSession) {
        this.conn = conn;
        this.sqlSession = sqlSession;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter, Class<E> mapperInterface) throws Exception {
        System.out.println("Start to execute SQL: " + ms.getSourceId() + " >>>");

        // Instantiate ReplacedParameterHandler Class
        ParameterHandler replacedParamHandler = new ReplacedParameterHandler(ms.getQueryStr());

        // Set replaced parameters
        String replacedSql = (String) replacedParamHandler.setParameters(ms.getParaType(), ms.getReplacedParamOrder(), parameter);

        // replace all "#{...}" with "?" in SQL
        replacedSql = replacedSql.replaceAll("#\\{([^#{}]*)}", "?");
        ms.setQueryStr(replacedSql);

        // Instantiate StatementHandler Class
        StatementHandler statementHandler = new DefaultStatementHandler(ms);

        // Get PreparedStatement from conn
        PreparedStatement preparedStatement = statementHandler.prepare(conn);

        // Instantiate PreparedParameterHandler Class
        SqlPreparedParameter preparedParameter = new SqlPreparedParameter(preparedStatement);

        // Set prepared parameters
        preparedParameter.prepare(ms.getParaType(), ms.getPreparedParamOrder(), parameter);

        // Executor SQL and get ResultSet
        ResultSet resultSet = statementHandler.execute(preparedStatement);

        // Instantiate ResultSetHandler Class and convert result to POJO
        ResultSetHandler resultSetHandler = new SqlResultSetHandler(ms, sqlSession);

        // Get SQL result
        List<E> result = resultSetHandler.handleResultSet(resultSet);
        if (ms.getCommandType().equals("select") && result.size() == 0)
            System.out.println("The result of SELECT is null");
        return result;
    }
}
