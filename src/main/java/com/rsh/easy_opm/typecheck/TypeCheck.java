package com.rsh.easy_opm.typecheck;

import com.rsh.easy_opm.error.AssertError;

import java.sql.Date;
import java.util.Collection;

public class TypeCheck {
    public static boolean isBoolean(Class<?> type) {
        if (Boolean.class == type)
            return true;
        if (boolean.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isCharacter(Class<?> type) {
        if (Character.class == type)
            return true;
        if (char.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isDouble(Class<?> type) {
        if (Double.class == type)
            return true;
        if (double.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isFloat(Class<?> type) {
        if (Float.class == type)
            return true;
        if (float.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isInteger(Class<?> type) {
        if (Integer.class == type)
            return true;
        if (int.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isLong(Class<?> type) {
        if (Long.class == type)
            return true;
        if (long.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isShort(Class<?> type) {
        if (Short.class == type)
            return true;
        if (short.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isByte(Class<?> type) {
        if (Byte.class == type)
            return true;
        if (byte.class == type)
            AssertError.notSupported("Non-encapsulated result type[" + type.getName() + ']');
        return false;
    }

    public static boolean isDate(Class<?> type) {
        return Date.class == type;
    }

    public static boolean isString(Class<?> type) {
        return String.class == type;
    }

    public static boolean isBasicType(Class<?> type) {
        return isInteger(type) || isByte(type) || isCharacter(type)
                || isDouble(type) || isFloat(type) || isBoolean(type)
                || isLong(type) || isShort(type) || isDate(type)
                || isString(type);
    }

    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
