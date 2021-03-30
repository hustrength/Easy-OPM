package com.rsh.easy_opm.json;

import com.rsh.easy_opm.error.AssertError;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonMapper {
    // when Constant INDENT is false, do not write indent
    private static boolean INDENT_ON = false;

    private static boolean OVERIDE_ON = false;

    public static boolean isIndentOn() {
        return INDENT_ON;
    }

    public static void setIndentOn(boolean indentOn) {
        JsonMapper.INDENT_ON = indentOn;
    }

    public static boolean isOverideOn() {
        return OVERIDE_ON;
    }

    public static void setOverideOn(boolean overideOn) {
        OVERIDE_ON = overideOn;
    }

    /* public methods */

    public <T> T readValueFromString(String json, Class<T> entityClass) {
        String parsedJson = json.replaceAll("\\s", "");
        try {
            return readValue(parsedJson, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T readValueFromFile(File file, Class<T> entityClass) {
        if (!file.exists()) {
            AssertError.warning("Fail to read value from a file, because the file[" + file.toString() + "] does not exist");
            return null;
        }

        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            AssertError.warning("Fail to read value from a file, because, the file size exceeds Integer.MAX_VALUE[" + Integer.MAX_VALUE + "] bytes");
            return null;
        }

        byte[] fileContent = new byte[(int) fileLength];
        try (FileInputStream in = new FileInputStream(file)) {
            if (in.read(fileContent) == -1) {
                AssertError.warning("Fail to read value from a file, because the file[" + file.toString() + "] is empty");
                return null;
            }

            String contentString = new String(fileContent);
            String parsedJson = contentString.replaceAll("\\s", "");
            return readValue(parsedJson, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeValueAsFile(File file, Object obj) {
        try {
            if (!OVERIDE_ON && !file.createNewFile()) {
                AssertError.warning("Fail to write value as a file, because the file[" + file.toString() + "] already exists");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter output = new PrintWriter(file)) {
            output.println(writeValueAsString(obj));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String writeValueAsString(Object obj) {
        try {
            String result = writeValueAsString(0, obj);
            if (INDENT_ON)
                result = result.substring(1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* private methods */

    private <T> T readValue(String json, Class<T> entityClass) throws Exception {
        if (json.equals("null"))
            return null;
        T entity = (T) entityClass.getConstructor().newInstance();
        Field[] fields = entityClass.getDeclaredFields();

        // regex for matching the whole entity
        String attribute = "\"([_a-zA-Z][_a-zA-Z0-9]*)\"";
        String collectionValue = "\\[(.*)]";
        String numberValue = "([^,\"}]*)";
        String stringValue = "\"([^,\"}]*)\"";
        String entityValue = "\\{([^,\"}]*)}";
        String singleValue = '(' + stringValue + '|' + entityValue + '|' + numberValue + ')';
        String value = '(' + collectionValue + '|' + singleValue + ')';
        String regex = attribute + ':' + value;
        Pattern p = Pattern.compile(regex);

        // regex for matching the collection in the entity
        String regexForCollection = "\\{([^}]*?)}";
        Pattern patternForCollection = Pattern.compile(regexForCollection);

        Matcher m = p.matcher(json);
        for (Field field :
                fields) {
            if (m.find()) {
                field.setAccessible(true);
                String fieldName = m.group(1);
                String fieldValue = m.group(2);
                String collectionVal = m.group(3);
                String stringVal = m.group(5);

                // extract actual value from Collection
                if (collectionVal != null)
                    fieldValue = collectionVal;
                    // extract actual value from String
                else if (stringVal != null)
                    fieldValue = stringVal;

                if (!fieldValue.equals("null")) {
                    if (!fieldName.equals(field.getName())) {
                        AssertError.warning("Field name[" + fieldName + "] in json is not matched with " + "Field name[" + field.getName() + "] in entity");
                    }
                    // parse the field and when the field is a basic type, readBasicClass returns true
                    else if (!readBasicClass(fieldValue, field, entity)) {
                        // when the field type is Array
                        if (field.getType().isArray()) {
                            Matcher matcherForArray = patternForCollection.matcher(fieldValue);

                            long numOfEntity = matcherForArray.results().count();
                            if (numOfEntity > Integer.MAX_VALUE) {
                                AssertError.warning("The number of entities in an array exceeds Integer.MAX_VALUE[" + Integer.MAX_VALUE + "], so only Integer.MAX_VALUE Objects are read");
                            }
                            Object[] objectArray = new Object[(int) numOfEntity];
                            for (int i = 0; i < numOfEntity; i++) {
                                if (matcherForArray.find()) {
                                    objectArray[i] = readValue(matcherForArray.group(1), field.getType().getComponentType());
                                }
                            }
                            if (numOfEntity > 0)
                                field.set(entity, objectArray);
                        }
                        // when the field type is List
                        else if (List.class.isAssignableFrom(field.getType())) {
                            Matcher matcherForList = patternForCollection.matcher(fieldValue);
                            List<Object> objectList = new ArrayList<>();

                            String genericType = field.getGenericType().getTypeName();
                            String regexForGenericType = "<([^>]*)>";
                            Pattern patternForGenericType = Pattern.compile(regexForGenericType);
                            Matcher matcherForGenericType = patternForGenericType.matcher(genericType);

                            String elementType = null;
                            if (matcherForGenericType.find()) {
                                elementType = matcherForGenericType.group(1);
                            }

                            while (matcherForList.find()) {
                                Object result = readValue(matcherForList.group(1), Class.forName(elementType));
                                objectList.add(result);
                            }
                            if (!objectList.isEmpty())
                                field.set(entity, objectList);
                        }
                        // when the field type is single class
                        else field.set(entity, readValue(fieldValue, field.getType()));
                    }
                }
            }
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    private String writeValueAsString(int indentLevel, Object obj) throws Exception {
        StringBuilder result = new StringBuilder();
        if (isArray(obj)) {
            result.append('[');
            for (Object ele :
                    (Object[]) obj) {
                writeSingleEntity(indentLevel, result, ele);
                result.append(',');
            }
            result.delete(result.length() - 1, result.length());
            result.append(']');
        } else if (isCollection(obj)) {
            result.append('[');
            for (Object ele :
                    (Collection<Object>) obj) {
                writeSingleEntity(indentLevel, result, ele);
                result.append(',');
            }
            result.delete(result.length() - 1, result.length());
            result.append(']');
        } else writeSingleEntity(indentLevel, result, obj);

        return result.toString();
    }

    private void writeSingleEntity(int indentLevel, StringBuilder result, Object obj) throws Exception {
        if (obj == null) {
            result.append("null");
            return;
        }

        writeIndent(result, indentLevel);
        result.append('{');

        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field :
                fields) {
            writeIndent(result, indentLevel);

            field.setAccessible(true);
            Object member = field.get(obj);
            String fieldName = field.getName();
            result.append('\"').append(fieldName).append('\"').append(':');
            if (!writeBasicClass(result, member))
                result.append(writeValueAsString(indentLevel + 1, member));
            result.append(',');
        }
        result.delete(result.length() - 1, result.length());
        writeIndent(result, indentLevel);
        result.append('}');
    }

    private boolean readBasicClass(String fieldValue, Field field, Object entity) throws Exception {
        String fieldType = field.getType().getSimpleName();
        switch (fieldType) {
            case "Date":
                field.set(entity, Date.valueOf(fieldValue));
                return true;
            case "String":
                field.set(entity, fieldValue);
                return true;
            case "Integer":
            case "int":
                field.set(entity, Integer.valueOf(fieldValue));
                return true;
            case "Boolean":
            case "boolean":
                field.set(entity, Boolean.valueOf(fieldValue));
                return true;
            case "Float":
            case "float":
                field.set(entity, Float.valueOf(fieldValue));
                return true;
            case "Character":
            case "char":
                field.set(entity, fieldValue.charAt(0));
                return true;
            case "Byte":
            case "byte":
                field.set(entity, Byte.valueOf(fieldValue));
                return true;
            case "Short":
            case "short":
                field.set(entity, Short.valueOf(fieldValue));
                return true;
            case "Long":
            case "long":
                field.set(entity, Long.valueOf(fieldValue));
                return true;
            case "Double":
            case "double":
                field.set(entity, Double.valueOf(fieldValue));
                return true;
            /* add more basic classes below */

            // it is object class, return false
            default:
                return false;
        }
    }

    private boolean writeBasicClass(StringBuilder result, Object member) {
        if (member instanceof Number) {
            result.append(member);
            return true;
        }
        if (member instanceof Boolean) {
            result.append(member);
            return true;
        }
        if (member instanceof Character) {
            result.append('\'');
            result.append(member);
            result.append('\'');
            return true;
        }
        if (member instanceof String) {
            result.append('\"');
            result.append(member);
            result.append('\"');
            return true;
        }
        if (member instanceof Date) {
            result.append('\"');
            result.append(member.toString());
            result.append('\"');
            return true;
        }
        /* add more basic classes below */

        return false;
    }

    private void writeIndent(StringBuilder result, int indentLevel) {
        if (!INDENT_ON)
            return;
        result.append('\n');
        for (int i = 0; i < indentLevel; i++) {
            result.append("\t");
        }
    }

    private boolean isArray(Object obj) {
        if (obj == null)
            return false;
        return obj.getClass().isArray();
    }

    private boolean isCollection(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof Collection;
    }
}
