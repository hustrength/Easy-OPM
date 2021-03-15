package com.rsh.easy_opm.reflection;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class ReflectionUtil {
    @SuppressWarnings("unchecked")
    public static <E> Object convertToBean(String classType, ResultSet resultSet) {
        try {
            Class<?> entityClass = Class.forName(classType);
            E entity = (E) entityClass.newInstance();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                switch (field.getType().getSimpleName()) {
                    case "String":
                        field.set(entity, resultSet.getString(field.getName()));
                        break;
                    case "int":
                        field.set(entity, resultSet.getInt(field.getName()));
                        break;
                    case "boolean":
                        field.set(entity, resultSet.getBoolean(field.getName()));
                        break;
                    case "float":
                        field.set(entity, resultSet.getFloat(field.getName()));
                        break;
                    case "char":
                        field.set(entity, resultSet.getByte(field.getName()));
                    default:
                        assert false : "Type[" + field.getType().getSimpleName() + "] is not supported";
                }
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
