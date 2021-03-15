package com.rsh.easy_opm.error;

public class AssertError {
    public static void notFoundError(boolean found, String target) {
        assert found : target + " Not Found";
    }

    public static void notFoundError(boolean found, String target, String path) {
        assert found : target + " Not Found In " + path;
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue, String target, String targetValue) {
        assert matched : source + "[" + sourceValue + "]" + " Not Matched With" + target + "[" + targetValue + "]";
    }

    public static void notMatchedError(boolean matched, String source, String sourceValue) {
        assert matched : source + "[" + sourceValue + "]" + " Not Matched";
    }

    public static void notNullPointer(boolean notNull, String target) {
        assert notNull : "The pointer of " + target + " is null";
    }
}
