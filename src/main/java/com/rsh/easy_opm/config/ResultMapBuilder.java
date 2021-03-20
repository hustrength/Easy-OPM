package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapBuilder {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Map<String, String> aliasMap;
    private String collectionId;
    private final Map<String, String> collectionsProperty = new HashMap<>();

    public Map<String, String> getResultMap(String sourceID) {
        AssertError.notFoundError(resultMaps.containsKey(sourceID), sourceID, "Result Maps");
        return resultMaps.get(sourceID);
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getCollectionProperty(String sourceID) {
        AssertError.notFoundError(this.resultMaps.containsKey(sourceID), sourceID, "collectionsProperty of resultMaps");
        return collectionsProperty.get(sourceID);
    }

    public ResultMapBuilder(Element rootNode, Map<String, String> aliasMap) {
        AssertError.notNullPointer(rootNode != null, "mapper");

        // store alias type map
        this.aliasMap = aliasMap;

        List<Element> resultMapList = rootNode.elements("resultMap");
        for (Element resultMapNode :
                resultMapList) {
            String id = resultMapNode.attributeValue("id");

            Map<String, String> resultMap = new HashMap<>();

            // parse id node
            Element idNode = resultMapNode.element("id");
            if (idNode != null) {
                String idColumn = idNode.attributeValue("column");
                String idProperty = idNode.attributeValue("property");
                resultMap.put(idProperty, idColumn);
                collectionId = idProperty;
            }

            // parse result node
            List<Element> resultList = resultMapNode.elements("result");
            for (Element resultNode :
                    resultList) {
                String column = resultNode.attributeValue("column");
                String property = resultNode.attributeValue("property");
                resultMap.put(property, column);
            }

            // parse association node
            List<Element> associations = resultMapNode.elements("association");
            for (Element association :
                    associations) {
                parseAssociation(association, resultMap);
            }

            // parse collection node
            // Collection is not allowed to be multiple, so only the 1st Collection node will be parsed
            Element collection = resultMapNode.element("collection");
            if (collection != null) {
                    String collectionProperty = parseCollection(collection, resultMap);
                collectionsProperty.put(id, collectionProperty);
            }

            // put resultMap into resultMaps
            resultMaps.put(id, resultMap);
        }
    }

    private String parseCollection(Element ele, Map<String, String> resultMap) {
        String property = ele.attributeValue("property");
        parseUnion(ele, resultMap);
        // Collection is not allowed to iterate

        return property;
    }

    private void parseAssociation(Element ele, Map<String, String> resultMap) {
        // do not need to use the result: ofType
        parseUnion(ele, resultMap);

        // continue to parse association
        List<Element> associations = ele.elements("association");
        for (Element association :
                associations) {
            parseAssociation(association, resultMap);
        }
    }

    private void parseUnion(Element ele, Map<String, String> resultMap) {
        if (ele == null)
            return;
        String ofType = ele.attributeValue("ofType");

        // replace alias with real type
        if (aliasMap.containsKey(ofType))
            ofType = aliasMap.get(ofType);

        List<Element> resultList = ele.elements("result");
        for (Element resultNode :
                resultList) {
            String column = resultNode.attributeValue("column");
            String property = resultNode.attributeValue("property");
            // for association, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            resultMap.put(property + '@' + ofType, column);
        }
    }
}
