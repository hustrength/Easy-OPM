package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapBuilder {
    Map<String, Map<String, String>> resultMaps = new HashMap<>();

    public ResultMapBuilder(Element rootNode) {
        AssertError.notNullPointer(rootNode != null, "mapper");
        List<Element> resultMapList = rootNode.elements("resultMap");
        for (Element resultMapNode :
                resultMapList) {
            String id = resultMapNode.attributeValue("id");
            List<Element> resultList = resultMapNode.elements("result");
            Map<String, String> resultMap = new HashMap<>();
            for (Element resultNode :
                    resultList) {
                String column = resultNode.attributeValue("column");
                String property = resultNode.attributeValue("property");
                resultMap.put(property, column);
            }
            this.resultMaps.put(id, resultMap);
        }
    }

    public Map<String, String> getResultMap(String sourceID){
        AssertError.notFoundError(this.resultMaps.containsKey(sourceID), sourceID, "Result Maps");
        return this.resultMaps.get(sourceID);
    }
}
