package com.rsh.config;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class InitConfigAndMapperTest {
    @Test
    public void testInit() {
        String dbSourceID = "mysql_developer";
        InitConfigAndMapper init = new InitConfigAndMapper(dbSourceID);
        Configuration config = init.getConfig();
        Map<String, MappedStatement> mappers = init.getMapperStatements();

        System.out.println(config);
        System.out.println();

        Set<String> mapperKeys = mappers.keySet();
        for (String cur :
                mapperKeys) {
            System.out.println(cur + " = " + mappers.get(cur).toString());
        }
    }
}
