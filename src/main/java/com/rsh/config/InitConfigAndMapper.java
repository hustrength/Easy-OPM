package com.rsh.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
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
        Element root = readXML();

        // parse elements
        parseProperties(root.element("properties"));
        parseEnvironments(root.element("environments"));
        parseMappers(root.element("mappers"));

        // check if every configuration is set
        config.checkConfig();
    }

    public InitConfigAndMapper() {
    }


    private Element readXML() {
        URL resources = InitConfigAndMapper.class.getClassLoader().getResource(Configuration.EASYOPM_CONFIG_PATH);
        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(resources);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document.getRootElement();
    }

    private void parseProperties(Element ele) {
        String propertyPath = ele.attributeValue("resources");
        InputStream configIn = InitConfigAndMapper.class.getClassLoader().getResourceAsStream(propertyPath);
        try {
            propertiesVar.load(configIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseEnvironments(Element ele) {
        List<Element> environments = ele.elements("environment");
        for (Element cur :
                environments) {
            String id = cur.attributeValue("id");
            if(id.equals(config.getDbSourceID())) {
                parseDataSource(cur.element("dataSource"));
            }
        }
    }

    private void parseDataSource(Element ele) {
        String type = ele.attributeValue("type");
        List<Element> properties = ele.elements("property");
        Map<String, String> propertiesMap = new HashMap<>();
        for (Element cur :
                properties) {
            String name = cur.attributeValue("name");
            String value = cur.attributeValue("value");
            String pattern = "\\$\\{(.*)\\}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(value);
            if (m.find()){
                value = propertiesVar.getProperty(m.group(0));
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
        URL resources = InitConfigAndMapper.class.getClassLoader().getResource(Configuration.MAPPER_FILE_PATH);
        assert resources != null;
        File mappers = new File(resources.getFile());
        assert mappers.isDirectory();
        File[] listFiles = mappers.listFiles();
        for (File file : listFiles) {
            parseMapperFile(file);
        }
    }

    public void parseMapperFile(File file) {

        // parse Document
//        Element node = document.getRootElement();
//        String namespace = node.attribute("namespace").getData().toString();
//        List<Element> selects = node.elements("select");
//        for (Element element : selects) {
//            MappedStatement mappedStatement = new MappedStatement();
//            String id = element.attribute("id").getData().toString();
//            String resultType = element.attribute("resultType").getData().toString();
//            String sql = element.getData().toString();
//            String sourceId = namespace + "." + id;
//            mappedStatement.setSourceId(sourceId);
//            mappedStatement.setResultType(resultType);
//            mappedStatement.setSql(sql);
//            mappedStatement.setNamespace(namespace);
//
//            // register the mapper into mapperStatments
//            this.mapperStatements.put(sourceId, mappedStatement);
//        }
    }
}
