package com.rsh.easy_opm.config;

import com.rsh.easy_opm.annotation.*;
import com.rsh.easy_opm.error.AssertError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationParser {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Class<?> mapperInterface;
    private Annotation select;

    public AnnotationParser(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public MappedStatement parse(Method method) {
        MappedStatement ms = new MappedStatement();
        boolean foundAnnotation = false;

        foundAnnotation = parseSqlAnnotation(method, ms);

        if (method.isAnnotationPresent(Insert.class)) {
            Insert insert = method.getAnnotation(Insert.class);
            ms.setSql(insert.sql());
            ms.setCommandType("insert");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Delete.class)) {
            Delete delete = method.getAnnotation(Delete.class);
            ms.setSql(delete.sql());
            ms.setCommandType("delete");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            ms.setSql(update.sql());
            ms.setCommandType("update");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(ResultType.class)) {
            ResultType resultType = method.getAnnotation(ResultType.class);
            ms.setResultType(resultType.value().getName());
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Results.class)) {
            if (method.isAnnotationPresent(ResultMap.class))
                System.out.println("WARNING: @Results and @ResultMap are both set in " + mapperInterface.getName() + ". Use @Results in priority");

            Map<String, String> resultMap = new HashMap<>();
            Results results = method.getAnnotation(Results.class);
            Result[] resultList = results.value();
            String resultsID = results.id();
            for (Result result :
                    resultList) {
                resultMap.put(result.property(), result.column());
            }
            resultMaps.put(resultsID, resultMap);
            ms.setResultMap(resultMap);
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

    @SuppressWarnings("unchecked")
    private boolean parseSqlAnnotation(Method method, MappedStatement ms) {
        String[] sqlCommands = {"Select", "Insert", "Update", "Delete"};
        String packageRoot = "com.rsh.easy_opm.annotation";
        for (String sqlCommand :
                sqlCommands) {
            try {
                String sqlAnnotationName = packageRoot + '.' + sqlCommand;
                Class<?> clz = Class.forName(sqlAnnotationName);

//                Annotation[] annotations = clz.getDeclaredAnnotations();
//                for (Annotation annotation :
//                        annotations) {
//                    System.out.println(annotation.toString());
//                }
//                Annotation select = method.getAnnotation(Select.class);


                if (method.isAnnotationPresent((Class<? extends Annotation>) clz)) {
                    Object entity = clz.getName();
                    Field field = clz.getField("sql");
                    String sql = (String) field.get(entity);

                    // replace all "#{...}" with "?" in SQL
                    String parsedSql = sql.replaceAll("#\\{([^#{}]*)}", "?");

                    // parse Prepared Params #{...} in SQL
                    List<String> preparedParamOrder = MapperBuilder.parsePreparedParams(sql);

                    // parse Replaced Params ${...} in SQL
                    List<String> replacedParamOrder = MapperBuilder.parseReplacedParams(sql);

                    ms.setSql(parsedSql);
                    ms.setPreparedParamOrder(preparedParamOrder);
                    ms.setReplacedParamOrder(replacedParamOrder);
                    ms.setCommandType("select");

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if (method.isAnnotationPresent(Select.class)) {
//            // @resultType Attr is necessary in @select
//            AssertError.notFoundError(method.isAnnotationPresent(ResultType.class), "resultType", "Select Node in " + mapperInterface.getName());
//
//            Select select = method.getAnnotation(Select.class);
//            String sql = select.sql();
//
//            // replace all "#{...}" with "?" in SQL
//            String parsedSql = sql.replaceAll("#\\{([^#{}]*)}", "?");
//
//            // parse Prepared Params #{...} in SQL
//            List<String> preparedParamOrder = MapperBuilder.parsePreparedParams(sql);
//
//            // parse Replaced Params ${...} in SQL
//            List<String> replacedParamOrder = MapperBuilder.parseReplacedParams(sql);
//
//            ms.setSql(parsedSql);
//            ms.setPreparedParamOrder(preparedParamOrder);
//            ms.setReplacedParamOrder(replacedParamOrder);
//            ms.setCommandType("select");
//            return true;
//        }
        return false;
    }
}
