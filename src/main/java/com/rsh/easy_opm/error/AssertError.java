package com.rsh.easy_opm.error;

public class AssertError {
    public static void notFoundError(boolean found, String target) {
        assert found : target + " not found";
    }

    public static void notFoundError(boolean found, String target, String path) {
        assert found : target + " not found in [" + path + ']';
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue, String target, String targetValue) {
        assert matched : source + "[" + sourceValue + "]" + " not matched with " + target + "[" + targetValue + "]";
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue, String target, String targetValue, String path) {
        assert matched : source + "[" + sourceValue + "]" + " not matched with " + target + "[" + targetValue + "] in [" + path + ']';
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue) {
        assert matched : source + "[" + sourceValue + "]" + " not matched";
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue, String path) {
        assert matched : source + "[" + sourceValue + "]" + " not matched in [" + path + ']';
    }

    public static void notNullPointer(boolean notNull, String target) {
        assert notNull : "The pointer of " + target + " is null";
    }

    public static void notSupported(String source, String value) {
        assert false : source + '[' + value + "] not supported ";
    }

    public static void notSupported(String source) {
        assert false : source + " not supported ";
    }

    public static void warning(String statement) {
        System.out.println("\n\033[31m" + "WARNING: " + statement + "\033[0m\n");
    }
}
