package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultMapBuilder {
    private final Map<String, Map<String, String>> resultMaps = new HashMap<>();
    private final Map<String, String> aliasMap;
    private final Map<String, List<ResultMapUnion>> unionsMap = new HashMap<>();
    private final Map<String, String> collectionIds = new HashMap<>();

    public String getCollectionId(String sourceId) {
        return collectionIds.getOrDefault(sourceId, null);
    }

    public Map<String, String> getResultMap(String sourceId) {
        AssertError.notFoundError(resultMaps.containsKey(sourceId), "resultMap ID[" + sourceId + "]", "Known ResultMaps");
        return resultMaps.get(sourceId);
    }

    public List<ResultMapUnion> getUnionList(String sourceId) {
        return unionsMap.getOrDefault(sourceId, null);
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

            List<ResultMapUnion> unions = new ArrayList<>();


            // parse id node
            // only the 1st Id node will be parsed
            List<Element> idNodes = resultMapNode.elements("id");
            if (idNodes.size() == 1) {
                Element idNode = idNodes.get(0);
                String idColumn = idNode.attributeValue("column");
                String idProperty = idNode.attributeValue("property");
                resultMap.put(idProperty, idColumn);

                // only when collection node exists, assign the collectionId
                if (resultMapNode.element("collection") != null)
                    collectionIds.put(id, idProperty);
            } else if (idNodes.size() > 1) {
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

            // NOTICE: Collection node must be parsed before association node,
            //         because only the unionOfType of the 1st union will be fetched, when reflection module works

            // parse collection node
            // Collection is not allowed to be multiple, so only the 1st Collection node will be parsed
            List<Element> collections = resultMapNode.elements("collection");
            if (collections.size() == 1) {
                Element collection = collections.get(0);
                ResultMapUnion union = new ResultMapUnion();
                unions.add(union);
                parseCollection(collection, resultMap, union);
            } else if (collections.size() > 1) {
                AssertError.warning("Only the 1st Collection node will be parsed");
            }

            // parse association node
            // NOTICE: Association Node is not allowed to iterate in Annotation setting but do in XML setting.
            List<Element> associations = resultMapNode.elements("association");
            for (Element association :
                    associations) {
                ResultMapUnion union = new ResultMapUnion();
                unions.add(union);
                parseAssociation(association, resultMap, union);
            }

            // put resultMap into resultMaps
            resultMaps.put(id, resultMap);
            // put non-empty unions into unionsMap
            if (!unions.isEmpty())
                unionsMap.put(id, unions);
        }
    }

    private void parseCollection(Element ele, Map<String, String> resultMap, ResultMapUnion union) {


        parseUnion(ele, resultMap, union);
        // Collection is not allowed to iterate
    }

    private void parseAssociation(Element ele, Map<String, String> resultMap, ResultMapUnion union) {
        // do not need to use the result: ofType
        parseUnion(ele, resultMap, union);

        // continue to parse association
        List<Element> associations = ele.elements("association");
        for (Element association :
                associations) {
            parseAssociation(association, resultMap, union);
        }
    }

    private void parseUnion(Element ele, Map<String, String> resultMap, ResultMapUnion union) {
        if (ele == null)
            return;
        String ofTypeAttr = ele.attributeValue("ofType");
        // replace alias with real type
        if (aliasMap.containsKey(ofTypeAttr))
            ofTypeAttr = aliasMap.get(ofTypeAttr);

        // set select Attr and column Attr
        String selectAttr = ele.attributeValue("select");
        String columnAttr = ele.attributeValue("column");
        String propertyAttr = ele.attributeValue("property");

        union.setUnionOfType(ofTypeAttr);
        union.setUnionSelect(selectAttr);
        union.setUnionColumn(columnAttr);
        union.setUnionProperty(propertyAttr);

        List<Element> resultList = ele.elements("result");
        for (Element resultNode :
                resultList) {
            String column = resultNode.attributeValue("column");
            String property = resultNode.attributeValue("property");
            // for union, (property@ofType, column) consists of resultMap
            // for normal result, (property, column) consists of resultMap
            resultMap.put(property + '@' + ofTypeAttr, column);
        }
    }
}
