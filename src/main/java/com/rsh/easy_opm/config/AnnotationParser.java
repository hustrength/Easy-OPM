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
    private final Map<String, String> collectionsId = new HashMap<>();
    private final Map<String, String> collectionsProperty = new HashMap<>();

    private Map<String, String> getResultMap(String sourceId) {
        AssertError.notFoundError(resultMaps.containsKey(sourceId), "resultMap ID[" + sourceId + "] from @ResultMap", "known resultMaps in " + mapperInterface.getName());
        return resultMaps.get(sourceId);
    }

    private String getCollectionId(String sourceId) {
        return collectionsId.getOrDefault(sourceId, null);
    }

    private String getCollectionProperty(String sourceId) {
        return collectionsProperty.getOrDefault(sourceId, null);
    }

    public AnnotationParser(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;

        // parse @ResultMap in advance
        // NOTICE: when using @Repeatable, it is necessary to distinguish single @ResultMap condition and multiple @ResultMap condition

        // if using multiple @ResultMap, ResultMaps.class exists
        if (mapperInterface.isAnnotationPresent(ResultMaps.class)) {
            Map<String, String> nameMapper = new HashMap<>();
            ResultMaps results = mapperInterface.getAnnotation(ResultMaps.class);
            for (ResultMap resultMap :
                    results.value()) {
                parseResultMap(nameMapper, resultMap);
            }
            // if using single @ResultMap, ResultMap.class exists
        } else if (mapperInterface.isAnnotationPresent(ResultMap.class)) {
            Map<String, String> nameMapper = new HashMap<>();
            ResultMap resultMap = mapperInterface.getAnnotation(ResultMap.class);
            parseResultMap(nameMapper, resultMap);
        }
    }

    private void parseResultMap(Map<String, String> nameMapper, ResultMap resultMap) {
        // get Id attribute of resultMap
        String id = resultMap.id();

        // parse Id node
        AssertError.notFoundError(resultMap.idNode().length != 0, "idNode", "@resultMap in " + mapperInterface.getName());
        ResultsId resultsIdNode = resultMap.idNode()[0];

        // only the 1st Id node will be parsed
        if (resultMap.idNode().length > 1)
            AssertError.warning("Only the 1st Id node will be parsed");
        collectionsId.put(id, resultsIdNode.property());
        nameMapper.put(resultsIdNode.property(), resultsIdNode.column());

        // parse result node
        Result[] resultList = resultMap.result();
        for (Result result :
                resultList) {
            nameMapper.put(result.property(), result.column());
        }

        // parse collection node
        if (resultMap.collection().length != 0) {
            String collectionProp = parseCollection(nameMapper, resultMap);
            collectionsProperty.put(id, collectionProp);
        }

        // parse association node
        if (resultMap.association().length != 0){
            parseAssociation(nameMapper, resultMap);
        }

        resultMaps.put(id, nameMapper);
    }

    private String parseCollection(Map<String, String> nameMapper, ResultMap resultMap){
        // only the 1st Collection node will be parsed
        if (resultMap.collection().length > 1)
            AssertError.warning("Only the 1st Collection node will be parsed");

        Collection collection = resultMap.collection()[0];
        String collectionProp = collection.property();
        String collectionType = collection.ofType().getName();
        String collectionCol = collection.column();
        String collectionSelect = collection.select();
        Result[] collectionResults = collection.result();
        for (Result collectionResult :
                collectionResults) {
            // for union, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            nameMapper.put(collectionResult.property() + '@' + collectionType, collectionResult.column());
        }
        return collectionProp;
    }

    private void parseAssociation(Map<String, String> nameMapper, ResultMap resultMap){
        Association[] associations = resultMap.association();
        for (Association association :
                associations) {
            String associationProp = association.property();
            String associationType = association.ofType().getName();
            String associationCol = association.column();
            String associationSelect = association.select();
            Result[] associationResults = association.result();
            for (Result associationResult :
                    associationResults) {
                // for union, (property@ofType, column) consists of resultMap
                // for normal result, (property, column) consists of resultMap
                nameMapper.put(associationResult.property() + '@' + associationType, associationResult.column());
            }
        }
    }

    public MappedStatement parse(Method method) {
        MappedStatement ms = new MappedStatement();
        boolean foundAnnotation = false;

        String paramType = null;
        if (method.isAnnotationPresent(ParamType.class)) {
            ParamType type = method.getAnnotation(ParamType.class);
            Class<?> typeValue = type.value();
            if (Number.class.isAssignableFrom(typeValue) || String.class.isAssignableFrom(typeValue)) {
                paramType = "basic";
            } else if (typeValue.isAssignableFrom(Map.class)) {
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

        if (method.isAnnotationPresent(ResultMapId.class)) {
            ResultMapId resultMap = method.getAnnotation(ResultMapId.class);
            String resultsID = resultMap.value();
            ms.setResultMap(getResultMap(resultsID));
            ms.setCollectionId(getCollectionId(resultsID));
            ms.setCollectionProperty(getCollectionProperty(resultsID));
            foundAnnotation = true;
        }

        ms.setNamespace(mapperInterface.getName());
        ms.setSourceId(mapperInterface.getName() + '.' + method.getName());
        ms.checkMapperInfo();

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
