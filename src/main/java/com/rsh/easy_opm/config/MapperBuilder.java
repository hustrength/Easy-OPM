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
            System.out.println("Some XML syntax errors in " + resource);
            e.printStackTrace();
        }

        AssertError.notFoundError(document != null, path);
        Element rootNode = document.getRootElement();
        String namespace = rootNode.attributeValue("namespace");

        // find all typeAliases
        aliasMap = parseTypeAlias(rootNode);

        // find all resultMap nodes in mapper
        ResultMapBuilder resultMapBuilder = new ResultMapBuilder(rootNode, aliasMap);

        for (QueryCommandType cur :
                QueryCommandType.values()) {
            String commandType = cur.name().toLowerCase(Locale.ROOT);

            List<Element> commands = rootNode.elements(commandType);
            for (Element element : commands) {
                String id = element.attributeValue("id");
                String sourceId = namespace + '.' + id;
                String resultType = element.attributeValue("resultType");
                String queryStr = element.getText().trim();
                String resultMap = element.attributeValue("resultMap");

                // trim empty char in query string
                if (queryStr.charAt(0) == '\n')
                    queryStr = queryStr.substring(1);

                // resultType Attr is necessary in Select Node
                if (cur.equals(QueryCommandType.SELECT))
                    AssertError.notFoundError(resultType != null, "resultType", "Select Node in " + path);

                // build MappedStatement Class to store mapper info
                MappedStatement mappedStatement = new MappedStatement();

                // get resultMap and union settings
                if (resultMap != null) {
                    mappedStatement.setResultMap(resultMapBuilder.getResultMap(resultMap));
                    mappedStatement.setResultMapUnionList(resultMapBuilder.getUnionList(resultMap));
                    mappedStatement.setCollectionId(resultMapBuilder.getCollectionId(resultMap));
                }

                // parse Prepared Params #{...} in query sentence
                List<String> preparedParamOrder = parsePreparedParams(queryStr);

                // parse Replaced Params ${...} in query sentence
                List<String> replacedParamOrder = parseReplacedParams(queryStr);
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
                mappedStatement.setQueryStr(queryStr);
                mappedStatement.setCommandType(commandType);

                mappedStatement.checkMapperInfo();

                // register the mapper into mapperStatements
                mapInfo.put(sourceId, mappedStatement);
            }
        }
        return mapInfo;
    }

    public static List<String> parsePreparedParams(String queryStr) {
        return parseParams(queryStr, "#\\{([^#{}]*)}");
    }

    public static List<String> parseReplacedParams(String queryStr) {
        return parseParams(queryStr, "\\$\\{([^${}]*)}");
    }

    private static List<String> parseParams(String queryStr, String pattern){
        List<String> paraOrder = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(queryStr);
        while (m.find()) {
            paraOrder.add(m.group(1));
        }
        return paraOrder.size() != 0 ? paraOrder : null;
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
