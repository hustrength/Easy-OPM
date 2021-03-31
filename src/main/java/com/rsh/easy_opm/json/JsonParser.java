package com.rsh.easy_opm.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class JsonParser {
    private final String json;
    private String fieldName;
    private String fieldVal;
    private int start;
    private int end;
    private long curArraySize;
    private final Map<Character, Character> pair = new HashMap<>();
    Stack<Character> stack = new Stack<>();

    public JsonParser(String json) {
        this.json = json;
        pair.put('}', '{');
        pair.put(']', '[');
    }

    public long getCurArraySize() {
        return curArraySize;
    }

    public String getFieldName() {
        return fieldName.isEmpty() ? null : fieldName;
    }

    public String getFieldVal() {
        return fieldVal.isEmpty() ? null : fieldVal;
    }

    public boolean next() {
        if (!parseFieldName()) {
            fieldName = null;
            fieldVal = null;
            start = 0;
            end = 0;
            return false;
        }
        if (!parseFieldVal()) {
            fieldName = null;
            fieldVal = null;
            start = 0;
            end = 0;
            return false;
        }
        return true;
    }

    private boolean outOfRange(int index) {
        // json is invalid
        if (index >= json.length()) {
            fieldName = null;
            fieldVal = null;
            start = 0;
            end = 0;
            return true;
        }
        return false;
    }

    private boolean parseFieldName() {
        // find the beginning index of fieldName
        while (start < json.length() && json.charAt(start++) != '\"') ;

        if (outOfRange(start))
            return false;

        end = start;

        // find the end index of fieldName
        while (end < json.length() && json.charAt(end) != '\"') {
            end++;
        }

        if (outOfRange(end))
            return false;

        fieldName = json.substring(start, end);


        end++;
        start = end;
        return !outOfRange(end);
    }

    // parseFieldVal can also be used to fetch an element from a collection
    public boolean parseFieldVal() {
        // move to the beginning index of fieldVal
        end++;
        start = end;
        if (outOfRange(end))
            return false;
        curArraySize = 0;

        stack.clear();

        char firstChar = json.charAt(end);

        // if fieldVal is String or Number or char
        if (firstChar != '{' && firstChar != '[') {
            if (firstChar == '\"') {
                start++;
                end = start;
                if (outOfRange(end))
                    return false;
                while (end < json.length()) {
                    if (json.charAt(end) == '\"') {
                        // judge if it is the char '\"'
                        if (json.charAt(end - 1) != '\\')
                            break;
                    }
                    end++;
                }
                if (outOfRange(end))
                    return false;
                fieldVal = json.substring(start, end);
                end++;
                start = end;
                return true;
            } else if (firstChar == '\'') {
                end = end + 2;
                start++;
                if (json.charAt(end + 2) == '\'') {
                    fieldVal = json.substring(start, end);
                    end++;
                    start = end;
                    return true;
                } else return false;
            } else {
                while (end < json.length()) {
                    char num = json.charAt(end);
                    if (num < '0' || num > '9')
                        break;
                    end++;
                }
                // when the end index is not pointed to a number, json is invalid
                if (json.charAt(end) >= '0' && json.charAt(end) <= '9')
                    return false;
                fieldVal = json.substring(start, end);
                start = end;
                return true;
            }
        }

        while (end < json.length()) {
            char ch = json.charAt(end);
            // if ch is the left bracket
            if (pair.containsValue(ch)) {
                stack.push(ch);
            }// if ch is the right bracket
            else if (pair.containsKey(ch)) {
                if (stack.isEmpty())
                    return false;
                if (stack.peek() == pair.get(ch)) {
                    stack.pop();
                    // when a pair of bracket is matched and stack.size == 1, it means a whole entity is scanned
                    if (stack.size() == 1)
                        curArraySize++;
                    // when the right bracket is matched and the stack is empty, the matching finished
                    if (stack.isEmpty())
                        break;
                } else return false;
            }
            ++end;
        }
        // when the end index is not pointed to a right bracket, json is invalid
        if (!pair.containsKey(json.charAt(end)))
            return false;
        fieldVal = json.substring(start, end + 1);
        end++;
        start = end;
        return true;
    }
}
