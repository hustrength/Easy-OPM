package com.rsh.easy_opm.typecheck;

import java.sql.Date;
import java.util.Collection;

public class TypeCheck {
    public static boolean isBoolean(Class<?> type) {
        return Boolean.class == type || boolean.class == type;
    }

    public static boolean isCharacter(Class<?> type) {
        return Character.class == type || char.class == type;
    }

    public static boolean isDouble(Class<?> type) {
        return Double.class == type || double.class == type;
    }

    public static boolean isFloat(Class<?> type) {
        return Float.class == type || float.class == type;
    }

    public static boolean isInteger(Class<?> type){
        return Integer.class == type || int.class == type;
    }

    public static boolean isLong(Class<?> type){
        return Long.class == type || long.class == type;
    }

    public static boolean isShort(Class<?> type){
        return Short.class == type || short.class == type;
    }

    public static boolean isByte(Class<?> type){
        return Byte.class == type || byte.class == type;
    }

    public static boolean isDate(Class<?> type){
        return Date.class == type;
    }

    public static boolean isString(Class<?> type){
        return String.class == type;
    }

    public static boolean isBasicType(Class<?> type){
        return isInteger(type) || isByte(type) || isCharacter(type)
                || isDouble(type) || isFloat(type) || isBoolean(type)
                || isLong(type) || isShort(type) || isDate(type)
                || isString(type);
    }

    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
