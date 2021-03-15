package com.rsh.easy_opm.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MappedStatement {

    private String namespace;

    private String sourceId;

    private String sql;

    private String resultType;

    private String commandType;

    private Map<String, String> resultMap;

    private List<String> paraOrder;

    private String paraType;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public Map<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    public List<String> getParaOrder() {
        return paraOrder;
    }

    public void setParaOrder(List<String> paraOrder) {
        this.paraOrder = paraOrder;
    }

    public String getParaType() {
        return paraType;
    }

    public void setParaType(String paraType) {
        this.paraType = paraType;
    }

    @Override
    public String toString() {
        return "MappedStatement{" +
                "namespace='" + namespace + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                "\n, resultMap={" + printResultMap() + '}' +
                '}';
    }

    public String printResultMap(){
        if (this.resultMap == null) return null;
        Set<String> keySet= this.resultMap.keySet();
        StringBuffer resultString = new StringBuffer();
        for (String key :
                keySet) {
            resultString.append('(' + key + ", " + this.resultMap.get(key) + ") ");
        }
        return resultString.toString().trim();
    }
}
