package com.rsh.easy_opm.config;

public class MappedStatement {
    private String namespace;

    private String sourceId;

    private String sql;

    private String resultType;

    private String commandType;

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

    @Override
    public String toString() {
        return "MappedStatement{" +
                "namespace='" + namespace + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }
}
