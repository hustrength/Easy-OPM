package com.rsh.easy_opm.config;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class ConfigBuilderTest {
    @Test
    public void test() {
        ConfigBuilder init = new ConfigBuilder();
        Configuration config = init.getConfig();
        Map<String, MappedStatement> mappers = config.getMappedStatements();

        System.out.println(config);
        System.out.println();

        Set<String> mapperKeys = mappers.keySet();
        for (String cur :
                mapperKeys) {
            System.out.println(cur + " = " + mappers.get(cur).toString());
        }
    }
}
