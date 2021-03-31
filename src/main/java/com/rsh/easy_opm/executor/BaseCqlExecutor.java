package com.rsh.easy_opm.executor;

import com.rsh.easy_opm.config.MappedStatement;
import com.rsh.easy_opm.executor.parameter.CqlPreparedParameter;
import com.rsh.easy_opm.executor.parameter.ParameterHandler;
import com.rsh.easy_opm.executor.parameter.ReplacedParameterHandler;
import com.rsh.easy_opm.executor.resultset.CqlResultSetHandler;
import com.rsh.easy_opm.executor.resultset.ResultSetHandler;
import com.rsh.easy_opm.sqlsession.DefaultCqlSession;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

import static org.neo4j.driver.Values.parameters;

import java.util.List;

public class BaseCqlExecutor implements Executor {
    Driver driver;
    DefaultCqlSession cqlSession;

    public BaseCqlExecutor(Driver driver, DefaultCqlSession cqlSession) {
        this.driver = driver;
        this.cqlSession = cqlSession;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter, Class<E> mapperInterface) throws Exception {
        System.out.println("Start to execute CQL: " + ms.getSourceId() + " >>>");
        List<E> result;

        try (Session session = driver.session()) {
            // Instantiate ReplacedParameterHandler Class
            ParameterHandler replacedParamHandler = new ReplacedParameterHandler(ms.getQueryStr());

            // Set replaced parameters
            String replacedCql = (String) replacedParamHandler.setParameters(ms.getParaType(), ms.getReplacedParamOrder(), parameter);

            // replace "#{...}" with "{...}" in order to unify the usage of prepared params in RD and GD
            replacedCql = replacedCql.replaceAll("#\\{([^}]*)}", "\\$$1");
            ms.setQueryStr(replacedCql);

            // Set prepared parameters
            CqlPreparedParameter preparedParameter = new CqlPreparedParameter();
            Object[] cqlParam = preparedParameter.prepare(ms.getParaType(), ms.getPreparedParamOrder(), parameter);

            // Executor CQL and get result
            Result resultSet = session.run(replacedCql, parameters(cqlParam));

            // Instantiate ResultSetHandler Class and convert result to POJO
            ResultSetHandler resultSetHandler = new CqlResultSetHandler(ms, cqlSession);

            // Get SQL result
            result = resultSetHandler.handleResultSet(resultSet);
        }
        if (ms.getCommandType().equals("select") && result.size() == 0)
            System.out.println("The result of SELECT is null");
        return result;
    }
}
