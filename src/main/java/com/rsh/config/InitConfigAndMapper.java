package com.rsh.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class InitConfigAndMapper {
    private final Configuration config = new Configuration();
    private final Map<String, MappedStatement> mapperStatements = new HashMap<>();
    private final Properties propertiesVar = new Properties();

    public Configuration getConfig() {
        return config;
    }

    public Map<String, MappedStatement> getMapperStatements() {
        return mapperStatements;
    }

    public InitConfigAndMapper(String dbID) {
        config.setDbSourceID(dbID);
        // read a XML file and convert it to a Document
        Element root = loadXML();

        // parse elements
        parseProperties(root.element("properties"));
        parseEnvironments(root.element("environments"));
        parseMappers(root.element("mappers"));

        // check if every configuration is set
        config.checkConfig();
    }

    public InitConfigAndMapper() {
    }


    private Element loadXML() {
        URL resource = InitConfigAndMapper.class.getClassLoader().getResource(Configuration.EASYOPM_CONFIG_PATH);
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
        InputStream configIn = InitConfigAndMapper.class.getClassLoader().getResourceAsStream(propertyPath);
        try {
            propertiesVar.load(configIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseEnvironments(Element ele) {
        List<Element> environments = ele.elements("environment");
        Boolean dbSourceIdMatched = false;
        for (Element cur :
                environments) {
            String id = cur.attributeValue("id");
            if (id.equals(config.getDbSourceID())) {
                dbSourceIdMatched = true;
                parseDataSource(cur.element("dataSource"));
            }
        }
        assert dbSourceIdMatched : "Database source ID is incorrect";
    }

    private void parseDataSource(Element ele) {
        String type = ele.attributeValue("type");
        List<Element> properties = ele.elements("property");
        Map<String, String> propertiesMap = new HashMap<>();
        for (Element cur :
                properties) {
            String name = cur.attributeValue("name");
            String value = cur.attributeValue("value");
//            System.out.println("Parsing: Name[" + name + "] Value[" + value + "]");
            String pattern = "\\$\\{(.*)\\}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(value);
            if (m.find()) {
//                System.out.println("Try to replace [" + value + "] to [" + propertiesVar.getProperty(m.group(1)) + "]");
                value = propertiesVar.getProperty(m.group(1));
            }
            propertiesMap.put(name, value);
        }
        config.setDbConnType(type);
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
        URL resource = InitConfigAndMapper.class.getClassLoader().getResource(path);
        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(resource);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Element node = document.getRootElement();
        String namespace = node.attributeValue("namespace");
        List<Element> selects = node.elements("select");
        for (Element element : selects) {
            String id = element.attributeValue("id");
            String sourceId = namespace + "." + id;
            String resultType = element.attributeValue("resultType");
            String sql = element.getText();

            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setNamespace(namespace);
            mappedStatement.setSourceId(sourceId);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sql);

            // register the mapper into mapperStatments
            this.mapperStatements.put(sourceId, mappedStatement);
        }
    }
}
