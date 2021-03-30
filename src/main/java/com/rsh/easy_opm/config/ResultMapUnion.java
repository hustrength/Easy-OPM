package com.rsh.easy_opm.config;

/**
 * The infos of association node or collection node in resultMap
 */
public class ResultMapUnion {
    /**
     * The property used to designate the to-be-united field name of collection class.
     * <br/>
     * Shared with collection node and association node.
     */
    private String unionProperty;

    /**
     * The result of Union Class is from another SQL with the 'select' ID.
     * <br/>
     * Shared with collection node and association node.
     */
    private String unionSelect;

    /**
     * The column is the field name of the parameter that will be provided with next query SQL.
     * <br/>
     * NOTICE: The column is the field name in entity not in database.
     * Shared with collection node and association node.
     */
    private String unionColumn;

    /**
     * The class type of element of union Class.
     * <br/>
     * Shared with collection node and association node.
     */
    private String unionOfType;

    public String getUnionProperty() {
        return unionProperty;
    }

    public void setUnionProperty(String unionProperty) {
        this.unionProperty = unionProperty;
    }

    public String getUnionSelect() {
        return unionSelect;
    }

    public void setUnionSelect(String unionSelect) {
        this.unionSelect = unionSelect;
    }

    public String getUnionColumn() {
        return unionColumn;
    }

    public void setUnionColumn(String unionColumn) {
        this.unionColumn = unionColumn;
    }

    public String getUnionOfType() {
        return unionOfType;
    }

    public void setUnionOfType(String unionOfType) {
        this.unionOfType = unionOfType;
    }
}
