package com.rsh.easy_opm.config;

import com.rsh.easy_opm.error.AssertError;

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

    // symbol #{...}
    private List<String> preparedParamOrder;

    // symbol ${...}
    private List<String> replacedParamOrder;

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

    public List<String> getPreparedParamOrder() {
        return preparedParamOrder;
    }

    public void setPreparedParamOrder(List<String> preparedParamOrder) {
        this.preparedParamOrder = preparedParamOrder;
    }

    public List<String> getReplacedParamOrder() {
        return replacedParamOrder;
    }

    public void setReplacedParamOrder(List<String> replacedParamOrder) {
        this.replacedParamOrder = replacedParamOrder;
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

    public String printResultMap() {
        if (this.resultMap == null) return null;
        Set<String> keySet = this.resultMap.keySet();
        StringBuilder resultString = new StringBuilder();
        for (String key :
                keySet) {
            resultString.append('(').append(key).append(", ").append(this.resultMap.get(key)).append(") ");
        }
        return resultString.toString().trim();
    }

    public void CheckMapperInfo() {
        AssertError.notFoundError(namespace != null, "namespace", "Mapper[Source ID: "+ sourceId +']');
        AssertError.notFoundError(sourceId != null, "id", "Mapper[Source ID: "+ sourceId +']');
        AssertError.notFoundError(sql != null, "sql", "Mapper[Source ID: "+ sourceId +']');

        // when paraType is not given, the preparedParamOrder must be null
        if (paraType == null)
            AssertError.notMatchedError(preparedParamOrder == null, "parameterType", "null", "given parameters", "not null", sourceId);
    }

}
