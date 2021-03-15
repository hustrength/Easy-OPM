package com.rsh.easy_opm.config;

import com.rsh.easy_opm.User;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class TestConfigBuilder {
    @Test
    public void test() {
        /*ConfigBuilder init = new ConfigBuilder();
        Configuration config = init.getConfig();
        Map<String, MappedStatement> mappers = config.getMappedStatements();

        System.out.println(config);
        System.out.println();

        Set<String> mapperKeys = mappers.keySet();
        for (String cur :
                mapperKeys) {
            System.out.println(cur + " = " + mappers.get(cur).toString());
        }*/
        Class entityClass = User.class;
        try {
            User entity = (User) entityClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields){
            System.out.println(field.getType().getSimpleName());
            /*if (field.getType().getSimpleName().equals("String")) {
//                        ReflectionUtil.setPropToBean(entity, field.getName(), resultSet.getString(field.getName()));
            } else if (field.getType().getSimpleName().equals("int")) {
//                        ReflectionUtil.setPropToBean(entity, field.getName(), resultSet.getInt(field.getName()));
            }*/
        }
    }
}
