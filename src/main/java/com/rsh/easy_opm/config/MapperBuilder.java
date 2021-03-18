package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapperBuilder {
    // store all MappedStatement info
    Map<String, MappedStatement> mapInfo = new HashMap<>();
    // store all type alias info
    Map<String, String> aliasMap;

    public Map<String, MappedStatement> parseMapperFile(String path) {
        URL resource = ConfigBuilder.class.getClassLoader().getResource(path);
        AssertError.notFoundError(resource != null, path);
        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(resource);
        } catch (DocumentException e) {
            System.out.println("Fail to load " + resource);
            e.printStackTrace();
        }

        AssertError.notFoundError(document != null, path);
        Element rootNode = document.getRootElement();
        String namespace = rootNode.attributeValue("namespace");

        aliasMap = parseTypeAlias(rootNode);

        for (SqlCommandType cur :
                SqlCommandType.values()) {
            String commandType = cur.name().toLowerCase(Locale.ROOT);

            List<Element> commands = rootNode.elements(commandType);
            for (Element element : commands) {
                String id = element.attributeValue("id");
                String sourceId = namespace + "." + id;
                String resultType = element.attributeValue("resultType");
                String sql = element.getText();
                String resultMap = element.attributeValue("resultMap");

                // resultType Attr is necessary in Select Node
                if (cur.equals(SqlCommandType.SELECT))
                    AssertError.notFoundError(resultType != null, "resultType", "Select Node in " + path);

                // build MappedStatement Class to store mapper info
                MappedStatement mappedStatement = new MappedStatement();

                // use resultMap to set alias of db column
                if (resultMap != null) {
                    // find all resultMap nodes in mapper
                    ResultMapBuilder resultMapBuilder = new ResultMapBuilder(rootNode);
                    mappedStatement.setResultMap(resultMapBuilder.getResultMap(resultMap));
                }

                // parse Prepared Params #{...} in SQL
                List<String> preparedParamOrder = parsePreparedParams(sql);
                // replace all "#{...}" with "?" in SQL
                sql = sql.replaceAll("#\\{([^#{}]*)}", "?");

                // parse Replaced Params ${...} in SQL
                List<String> replacedParamOrder = parseReplacedParams(sql);
                String paraType = element.attributeValue("parameterType");

                // set result type alias according to typeAlias Properties
                if (aliasMap != null && aliasMap.containsKey(resultType))
                    mappedStatement.setResultType(aliasMap.get(resultType));
                else mappedStatement.setResultType(resultType);

                // set param type alias according to typeAlias Properties
                if (aliasMap != null && aliasMap.containsKey(paraType))
                    mappedStatement.setParaType(aliasMap.get(paraType));
                else mappedStatement.setParaType(paraType);

                mappedStatement.setPreparedParamOrder(preparedParamOrder);
                mappedStatement.setReplacedParamOrder(replacedParamOrder);
                mappedStatement.setNamespace(namespace);
                mappedStatement.setSourceId(sourceId);
                mappedStatement.setSql(sql);
                mappedStatement.setCommandType(commandType);

                // register the mapper into mapperStatements
                mapInfo.put(sourceId, mappedStatement);
            }
        }
        return mapInfo;
    }

    public static List<String> parsePreparedParams(String sql) {
        return parseParams(sql, "#\\{([^#{}]*)}");
    }

    public static List<String> parseReplacedParams(String sql) {
        return parseParams(sql, "\\$\\{([^${}]*)}");
    }

    private static List<String> parseParams(String sql, String pattern){
        List<String> paraOrder = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            paraOrder.add(m.group(1));
        }
        return paraOrder.size() != 0 ? paraOrder : null;
    }

    private static class ResultMapBuilder {
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

        public Map<String, String> getResultMap(String sourceID) {
            AssertError.notFoundError(this.resultMaps.containsKey(sourceID), sourceID, "Result Maps");
            return this.resultMaps.get(sourceID);
        }
    }

    private Map<String, String> parseTypeAlias(Element rootNode) {
        Element aliasesNode = rootNode.element("typeAliases");
        if (aliasesNode == null)
            return null;
        Map<String, String> aliases = new HashMap<>();
        List<Element> aliasNodes = aliasesNode.elements();
        if (aliasNodes == null)
            return null;
        for (Element alias :
                aliasNodes) {
            aliases.put(alias.attributeValue("alias"), alias.attributeValue("type"));
        }
        return aliases;
    }
}
