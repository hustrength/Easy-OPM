package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class ConfigBuilder {
    private final Configuration config = new Configuration();
    private final Properties propertiesVar = new Properties();


    public Configuration getConfig() {
        return config;
    }

    public ConfigBuilder() {
        // read a XML file and convert it to a Document
        Element root = loadXML();

        // parse elements
        parseProperties(root.element("properties"));
        parseEnvironments(root.element("environments"));
        parseMappers(root.element("mappers"));

        // check if every configuration is set
        config.checkConfig();
    }

    private Element loadXML() {
        URL resource = ConfigBuilder.class.getClassLoader().getResource(Configuration.EASYOPM_CONFIG_PATH);
        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(resource);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document.getRootElement();
    }

    private void parseProperties(Element ele) {
        String propertyPath = ele.attributeValue("resource");
        InputStream configIn = ConfigBuilder.class.getClassLoader().getResourceAsStream(propertyPath);
        try {
            propertiesVar.load(configIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseEnvironments(Element ele) {
        String defaultID = ele.attributeValue("default");
        List<Element> environments = ele.elements("environment");
        boolean dbSourceIdMatched = false;
        for (Element cur :
                environments) {
            String id = cur.attributeValue("id");
            if (id.equals(defaultID)) {
                dbSourceIdMatched = true;
                parseDataSource(cur.element("dataSource"));
            }
        }
        AssertError.notMatchedError(dbSourceIdMatched, "Default database source ID", defaultID);
    }

    private void parseDataSource(Element ele) {
        List<Element> properties = ele.elements("property");
        Map<String, String> propertiesMap = new HashMap<>();
        for (Element cur :
                properties) {
            String name = cur.attributeValue("name");
            String value = cur.attributeValue("value");
            String pattern = "\\$\\{([^\\#\\{\\}]*)\\}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(value);
            if (m.find()) {
                AssertError.notFoundError(propertiesVar.containsKey(m.group(1)), value, "Properties File");
                value = propertiesVar.getProperty(m.group(1));
            }
            propertiesMap.put(name, value);
        }
        config.setDbDriver(propertiesMap.get("driver"));
        config.setDbUrl(propertiesMap.get("url"));
        config.setDbUserName(propertiesMap.get("username"));
        config.setDbPassword(propertiesMap.get("password"));
    }

    private void parseMappers(Element ele) {
        List<Element> environments = ele.elements("mapper");
        for (Element cur :
                environments) {
            String resource = cur.attributeValue("resource");
            parseMapperFile(resource);
        }
    }

    public void parseMapperFile(String path) {
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
        AssertError.notFoundError(namespace != null, "namespace", "mapper");

        // find all resultMap nodes in mapper
        ResultMapBuilder resultMapBuilder = new ResultMapBuilder(rootNode);

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

                AssertError.notFoundError(id != null, "id", "mapper");
                AssertError.notFoundError(sql != null, "sql", "mapper");

                MappedStatement mappedStatement = new MappedStatement();
                // use resultMap when resultType is not given
                if (resultMap != null) {
                    mappedStatement.setResultMap(resultMapBuilder.getResultMap(resultMap));
                }

                // parse and bind parameters in SQL
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
                config.getMappedStatements().put(sourceId, mappedStatement);
            }
        }
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
