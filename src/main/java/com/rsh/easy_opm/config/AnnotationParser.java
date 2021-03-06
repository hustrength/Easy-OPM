package com.rsh.easy_opm.config;

import com.rsh.easy_opm.annotation.*;
import com.rsh.easy_opm.error.AssertError;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rsh.easy_opm.typecheck.TypeCheck.isBasicType;

public class AnnotationParser {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Class<?> mapperInterface;
    private final Map<String, List<ResultMapUnion>> unionsMap = new HashMap<>();
    private final Map<String, String> collectionIds = new HashMap<>();

    public String getCollectionId(String sourceId) {
        return collectionIds.getOrDefault(sourceId, null);
    }

    private Map<String, String> getResultMap(String sourceId) {
        AssertError.notFoundError(resultMaps.containsKey(sourceId), "resultMap ID[" + sourceId + "] from @ResultMap", "known resultMaps in " + mapperInterface.getName());
        return resultMaps.get(sourceId);
    }

    public List<ResultMapUnion> getUnionList(String sourceId) {
        return unionsMap.getOrDefault(sourceId, null);
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

        // parse result node
        Result[] resultList = resultMap.result();
        for (Result result :
                resultList) {
            nameMapper.put(result.property(), result.column());
        }

        List<ResultMapUnion> unions = new ArrayList<>();


        // parse Id node
        Result resultsIdNode = null;
        if (resultMap.idNode().length != 0) {
            resultsIdNode = resultMap.idNode()[0];
            // only the 1st Id node will be parsed
            if (resultMap.idNode().length > 1)
                AssertError.warning("Only the 1st Id node will be parsed");
            nameMapper.put(resultsIdNode.property(), resultsIdNode.column());
        }

        if (resultMap.collection().length != 0) {
            // only when collection node exists, assign the collectionId
            AssertError.notFoundError(resultsIdNode != null, "Collection Node exists, but idNode", "@resultMap in " + mapperInterface.getName());
            collectionIds.put(id, resultsIdNode.property());

            // parse collection node
            ResultMapUnion union = new ResultMapUnion();
            unions.add(union);
            parseCollection(nameMapper, resultMap, union);
        }

        // parse association node
        if (resultMap.association().length != 0) {
            parseAssociation(nameMapper, resultMap, unions);
        }

        resultMaps.put(id, nameMapper);
        if (!unions.isEmpty())
            unionsMap.put(id, unions);
    }

    private void parseCollection(Map<String, String> nameMapper, ResultMap resultMap, ResultMapUnion union) {
        // only the 1st Collection node will be parsed
        if (resultMap.collection().length > 1)
            AssertError.warning("Only the 1st Collection node will be parsed");

        Collection collection = resultMap.collection()[0];
        String collectionType = collection.ofType().getName();
        String collectionProp = collection.property();
        String collectionSelect = collection.select();
        String collectionCol = collection.column();

        union.setUnionOfType(setEmptyStrToNull(collectionType));
        union.setUnionProperty(setEmptyStrToNull(collectionProp));
        union.setUnionSelect(setEmptyStrToNull(collectionSelect));
        union.setUnionColumn(setEmptyStrToNull(collectionCol));

        Result[] collectionResults = collection.result();
        for (Result collectionResult :
                collectionResults) {
            // for union, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            nameMapper.put(collectionResult.property() + '@' + collectionType, collectionResult.column());
        }
    }

    private void parseAssociation(Map<String, String> nameMapper, ResultMap resultMap, List<ResultMapUnion> unions) {
        Association[] associations = resultMap.association();
        for (Association association :
                associations) {
            String associationProp = association.property();
            String associationType = association.ofType().getName();
            String associationSelect = association.select();
            String associationCol = association.column();

            ResultMapUnion union = new ResultMapUnion();
            unions.add(union);
            union.setUnionProperty(setEmptyStrToNull(associationProp));
            union.setUnionOfType(setEmptyStrToNull(associationType));
            union.setUnionSelect(setEmptyStrToNull(associationSelect));
            union.setUnionColumn(setEmptyStrToNull(associationCol));

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
            if (isBasicType(typeValue)) {
                paramType = "basic";
            } else if (typeValue.isAssignableFrom(Map.class)) {
                paramType = "map";
            } else {
                paramType = typeValue.getName();
            }
        }

        if (method.isAnnotationPresent(ResultType.class)) {
            ResultType resultType = method.getAnnotation(ResultType.class);
            ms.setResultType(resultType.value().getName());
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Select.class)) {
            // @resultType Attr is necessary in @select
            AssertError.notFoundError(ms.getResultType() != null, "resultType", "Select Node in " + mapperInterface.getName());

            Select select = method.getAnnotation(Select.class);
            parseQueryStr(ms, select.value(), paramType, "select");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Insert.class)) {
            Insert insert = method.getAnnotation(Insert.class);
            parseQueryStr(ms, insert.value(), paramType, "insert");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Delete.class)) {
            Delete delete = method.getAnnotation(Delete.class);
            parseQueryStr(ms, delete.value(), paramType, "delete");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            parseQueryStr(ms, update.value(), paramType, "update");
            foundAnnotation = true;
        }

        if (method.isAnnotationPresent(ResultMapId.class)) {
            ResultMapId resultMap = method.getAnnotation(ResultMapId.class);
            String resultsID = resultMap.value();
            ms.setResultMap(getResultMap(resultsID));
            ms.setResultMapUnionList(getUnionList(resultsID));
            ms.setCollectionId(getCollectionId(resultsID));
            foundAnnotation = true;
        }

        ms.setNamespace(mapperInterface.getName());
        ms.setSourceId(mapperInterface.getName() + '.' + method.getName());
        ms.checkMapperInfo();

        return foundAnnotation ? ms : null;
    }

    private void parseQueryStr(MappedStatement ms, String queryStr, String paramType, String commandType) {
        // parse Prepared Params #{...} in query sentence
        List<String> preparedParamOrder = MapperBuilder.parsePreparedParams(queryStr);

        // parse Replaced Params ${...} in query sentence
        List<String> replacedParamOrder = MapperBuilder.parseReplacedParams(queryStr);

        ms.setQueryStr(setEmptyStrToNull(queryStr));
        ms.setPreparedParamOrder(preparedParamOrder);
        ms.setReplacedParamOrder(replacedParamOrder);
        ms.setParaType(setEmptyStrToNull(paramType));
        ms.setCommandType(setEmptyStrToNull(commandType));
    }

    private String setEmptyStrToNull(String str) {
        if (str == null)
            return null;
        if (str.isEmpty())
            return null;
        return str;
    }
}
