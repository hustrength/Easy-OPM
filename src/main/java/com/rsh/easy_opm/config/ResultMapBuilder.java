package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapBuilder {
    Map<String, Map<String, String>> resultMaps = new HashMap<>();
    Map<String, List<String>> resultMapClassChains;
    Map<String, String> aliasMap;

    public ResultMapBuilder(Element rootNode, Map<String, String> aliasMap) {
        AssertError.notNullPointer(rootNode != null, "mapper");

        // store alias type map
        this.aliasMap = aliasMap;

        List<Element> resultMapList = rootNode.elements("resultMap");
        for (Element resultMapNode :
                resultMapList) {
            String id = resultMapNode.attributeValue("id");

            Map<String, String> resultMap = new HashMap<>();
            List<Element> resultList = resultMapNode.elements("result");
            for (Element resultNode :
                    resultList) {
                String column = resultNode.attributeValue("column");
                String property = resultNode.attributeValue("property");
                resultMap.put(property, column);
            }

            // parse collection
            List<String> classChain = parseCollection(rootNode.element("collection"), resultMap);
            if (classChain != null) {
                resultMapClassChains = new HashMap<>();
                resultMapClassChains.put(id, classChain);
            }
            resultMaps.put(id, resultMap);
        }
    }

    public Map<String, String> getResultMap(String sourceID) {
        AssertError.notFoundError(this.resultMaps.containsKey(sourceID), sourceID, "Result Maps");
        return this.resultMaps.get(sourceID);
    }

    private List<String> parseCollection(Element ele, Map<String, String> resultMap) {
        if (ele == null)
            return null;
        List<String> classChian = new ArrayList<>();
        String ofType = ele.attributeValue("ofType");

        // replace alias with real type
        if (aliasMap.containsKey(ofType))
            ofType = aliasMap.get(ofType);

        classChian.add(ofType);

        List<Element> resultList = ele.elements("result");
        for (Element resultNode :
                resultList) {
            String column = resultNode.attributeValue("column");
            String property = resultNode.attributeValue("property");
            // for collection, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            resultMap.put(property + '@' + ofType, column);
        }
        // continue to parse collection
        classChian.addAll(parseCollection(ele.element("collection"), resultMap));
        return classChian;
    }

    public List<String> getResultMapClassChain(String sourceID) {
        if (resultMapClassChains == null)
            return null;
        AssertError.notFoundError(resultMapClassChains.containsKey(sourceID), sourceID, "Result Maps");
        return resultMapClassChains.get(sourceID);
    }
}
