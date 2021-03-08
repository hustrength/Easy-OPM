package com.rsh.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class InitConfigAndMapper {
    private final Configuration config = new Configuration();
    private final Map<String, MappedStatement> mapperStatements = new HashMap<>();

    public Configuration getConfig() {
        return config;
    }

    public Map<String, MappedStatement> getMapperStatements() {
        return mapperStatements;
    }

    public InitConfigAndMapper() {
        loadConfig();
        loadMappers();
    }

    public void loadConfig(){
        InputStream configIn = InitConfigAndMapper.class.getClassLoader().getResourceAsStream(Configuration.CONFIG_FILE_PATH);
        Properties properties = new Properties();
        try{
            properties.load(configIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.setDbDriver(properties.get("jdbc.driver").toString());
        config.setDbPassword(properties.get("jdbc.password").toString());
        config.setDbUrl(properties.get("jdbc.url").toString());
        config.setDbUserName(properties.get("jdbc.username").toString());
    }

    public void loadMappers(){
        URL resources = InitConfigAndMapper.class.getClassLoader().getResource(Configuration.MAPPER_FILE_PATH);
        assert resources != null;
        File mappers = new File(resources.getFile());
        assert mappers.isDirectory();
        File[] listFiles = mappers.listFiles();
        for (File file : listFiles) {
            loadMapperInfo(file);
        }
    }

    private void loadMapperInfo(File file) {
        SAXReader reader = new SAXReader();

        // read a file and convert it to a Document
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // parse Document
        Element node = document.getRootElement();
        String namespace = node.attribute("namespace").getData().toString();
        List<Element> selects = node.elements("select");
        for (Element element : selects) {
            MappedStatement mappedStatement = new MappedStatement();
            String id = element.attribute("id").getData().toString();
            String resultType = element.attribute("resultType").getData().toString();
            String sql = element.getData().toString();
            String sourceId = namespace + "." + id;
            mappedStatement.setSourceId(sourceId);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sql);
            mappedStatement.setNamespace(namespace);

            // register the mapper into mapperStatments
            this.mapperStatements.put(sourceId, mappedStatement);
        }
    }
}
