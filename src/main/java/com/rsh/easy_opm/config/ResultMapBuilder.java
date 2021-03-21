package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapBuilder {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Map<String, String> aliasMap;
    private final Map<String, String> collectionsId = new HashMap<>();
    private final Map<String, String> collectionsProperty = new HashMap<>();

    public Map<String, String> getResultMap(String sourceId) {
        AssertError.notFoundError(resultMaps.containsKey(sourceId), "resultMap ID[" + sourceId + "]", "Known ResultMaps");
        return resultMaps.get(sourceId);
    }

    public String getCollectionId(String sourceId) {
        return collectionsId.getOrDefault(sourceId, null);
    }

    public String getCollectionProperty(String sourceId) {
        return collectionsProperty.getOrDefault(sourceId, null);
    }

    public ResultMapBuilder(Element rootNode, Map<String, String> aliasMap) {
        AssertError.notNullPointer(rootNode != null, "mapper");

        // store alias type map
        this.aliasMap = aliasMap;

        List<Element> resultMapList = rootNode.elements("resultMap");
        for (Element resultMapNode :
                resultMapList) {
            // get id attribute of resultMap node
            String id = resultMapNode.attributeValue("id");

            Map<String, String> resultMap = new HashMap<>();

            // parse id node
            // only the 1st Id node will be parsed
            List<Element> idNodes = resultMapNode.elements("id");
            if (idNodes.size() == 1) {
                Element idNode = idNodes.get(0);
                String idColumn = idNode.attributeValue("column");
                String idProperty = idNode.attributeValue("property");
                resultMap.put(idProperty, idColumn);
                collectionsId.put(id, idProperty);
            } else if (idNodes.size() > 1){
                AssertError.warning("Only the 1st Id node will be parsed");
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
            // NOTICE: Association Node is not allowed to iterate in Annotation setting but do in XML setting.
            List<Element> associations = resultMapNode.elements("association");
            for (Element association :
                    associations) {
                parseAssociation(association, resultMap);
            }

            // parse collection node
            // Collection is not allowed to be multiple, so only the 1st Collection node will be parsed
            List<Element> collections = resultMapNode.elements("collection");
            if (collections.size() == 1) {
                Element collection = collections.get(0);
                String collectionProperty = parseCollection(collection, resultMap);
                collectionsProperty.put(id, collectionProperty);
            } else if (collections.size() > 1) {
                AssertError.warning("Only the 1st Collection node will be parsed");
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
            // for union, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            resultMap.put(property + '@' + ofType, column);
        }
    }
}
