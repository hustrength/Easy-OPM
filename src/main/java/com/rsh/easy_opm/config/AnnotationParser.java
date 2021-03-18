package com.rsh.easy_opm.config;

import com.rsh.easy_opm.annotation.*;
import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationParser {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Class<?> mapperInterface;

    public AnnotationParser(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;

        // parse @Results in advance
        Method[] methods = mapperInterface.getDeclaredMethods();
        for (Method method :
                methods) {
            if (method.isAnnotationPresent(Results.class)) {
                Map<String, String> resultMap = new HashMap<>();
                Results results = method.getAnnotation(Results.class);
                Result[] resultList = results.value();
                String resultsID = results.id();
                for (Result result :
                        resultList) {
                    resultMap.put(result.property(), result.column());
                }
                resultMaps.put(resultsID, resultMap);
            }
        }
    }

    public MappedStatement parse(Method method) {
        MappedStatement ms = new MappedStatement();
        boolean foundAnnotation = false;

        String paramType = null;
        if (method.isAnnotationPresent(ParamType.class)){
            ParamType type = method.getAnnotation(ParamType.class);
            Class<?> typeValue = type.value();
            if (Number.class.isAssignableFrom(typeValue) || String.class.isAssignableFrom(typeValue)){
                paramType = "basic";
            } else if (typeValue.isAssignableFrom(Map.class)){
                paramType = "map";
            } else {
                paramType = typeValue.getName();
            }
        }
        if (method.isAnnotationPresent(Select.class)) {
            // @resultType Attr is necessary in @select
            AssertError.notFoundError(method.isAnnotationPresent(ResultType.class), "resultType", "Select Node in " + mapperInterface.getName());

            Select select = method.getAnnotation(Select.class);
            parseSqlCommand(ms, select.value(), paramType, "select");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Insert.class)) {
            Insert insert = method.getAnnotation(Insert.class);
            parseSqlCommand(ms, insert.value(), paramType, "insert");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Delete.class)) {
            Delete delete = method.getAnnotation(Delete.class);
            parseSqlCommand(ms, delete.value(), paramType, "delete");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            parseSqlCommand(ms, update.value(), paramType, "update");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(ResultType.class)) {
            ResultType resultType = method.getAnnotation(ResultType.class);
            ms.setResultType(resultType.value().getName());
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Results.class)) {
            if (method.isAnnotationPresent(ResultMap.class))
                System.out.println("\033[31m" + "WARNING: @Results and @ResultMap are both set in " + mapperInterface.getName() + ". Use @Results in priority" + "\033[0m");

            Results results = method.getAnnotation(Results.class);
            String resultsID = results.id();
            AssertError.notFoundError(resultMaps.containsKey(resultsID), "resultsID from @ResultMap", "known resultMaps in " + mapperInterface.getName());
            ms.setResultMap(resultMaps.get(resultsID));
            foundAnnotation = true;
        } else if (method.isAnnotationPresent(ResultMap.class)) {
            ResultMap resultMap = method.getAnnotation(ResultMap.class);
            String resultsID = resultMap.value();
            AssertError.notFoundError(resultMaps.containsKey(resultsID), "resultsID from @ResultMap", "known resultMaps in " + mapperInterface.getName());
            ms.setResultMap(resultMaps.get(resultsID));
            foundAnnotation = true;
        }

        return foundAnnotation ? ms : null;
    }

    private void parseSqlCommand(MappedStatement ms, String sql, String paramType, String commandType) {
        // replace all "#{...}" with "?" in SQL
        String parsedSql = sql.replaceAll("#\\{([^#{}]*)}", "?");

        // parse Prepared Params #{...} in SQL
        List<String> preparedParamOrder = MapperBuilder.parsePreparedParams(sql);

        // parse Replaced Params ${...} in SQL
        List<String> replacedParamOrder = MapperBuilder.parseReplacedParams(sql);

        ms.setSql(parsedSql);
        ms.setPreparedParamOrder(preparedParamOrder);
        ms.setReplacedParamOrder(replacedParamOrder);
        ms.setParaType(paramType);
        ms.setCommandType(commandType);
    }
}
