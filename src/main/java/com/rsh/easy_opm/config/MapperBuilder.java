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
    public Map<String, MappedStatement> parseMapperFile(String path) {
        URL resource = ConfigBuilder.class.getClassLoader().getResource(path);
        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(resource);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        AssertError.notFoundError(document != null, path);
        Element rootNode = document.getRootElement();
        String namespace = rootNode.attributeValue("namespace");
        AssertError.notFoundError(namespace != null, "namespace", path);

        // find all resultMap nodes in mapper
        ResultMapBuilder resultMapBuilder = new ResultMapBuilder(rootNode);

        // store all MappedStatement info
        Map<String, MappedStatement> map = new HashMap<>();

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

                AssertError.notFoundError(id != null, "id", path);
                AssertError.notFoundError(sql != null, "sql", path);
                if (cur.equals(SqlCommandType.SELECT))
                    AssertError.notFoundError(resultType != null, "resultType", "Select Node in " + path);

                MappedStatement mappedStatement = new MappedStatement();
                // use resultMap to set alias of db column
                if (resultMap != null) {
                    mappedStatement.setResultMap(resultMapBuilder.getResultMap(resultMap));
                }

                // get parameterType Attr and bind Paras in SQL
                List<String> paraOrder = parseParameters(sql);
                String paraType = element.attributeValue("parameterType");

                // when paraType is not given, the paraOrder must be null
                if (paraType == null)
                    AssertError.notMatchedError(paraOrder == null, "parameterType", paraType, "given parameters", "not null", sourceId);

                // replace all "#{...}" with "?" in SQL
                sql = sql.replaceAll("#\\{([^\\#\\{\\}]*)\\}", "?");

                mappedStatement.setParaOrder(paraOrder);
                mappedStatement.setParaType(paraType);
                mappedStatement.setNamespace(namespace);
                mappedStatement.setSourceId(sourceId);
                mappedStatement.setResultType(resultType);
                mappedStatement.setSql(sql);
                mappedStatement.setCommandType(commandType);

                // register the mapper into mapperStatments
                map.put(sourceId, mappedStatement);
            }
        }
        return map;
    }
    private List<String> parseParameters(String sql) {
        List<String> paraOrder = new ArrayList<>();
        String pattern = "#\\{([^\\#\\{\\}]*)\\}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            paraOrder.add(m.group(1));
        }
        return paraOrder.size() != 0 ? paraOrder : null;
    }
}
