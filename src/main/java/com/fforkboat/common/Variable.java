package com.fforkboat.common;

public class Variable {
    private String name;
    private VariableType variableType;
    private String value;

    private boolean isAssignedType = false;

    public Variable(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public VariableType getVariableType() {
        return variableType;
    }
    public void setVariableType(VariableType variableType) {
        if (isAssignedType)
            // TODO


        this.variableType = variableType;
        isAssignedType = true;
    }

    public String getValue() {
        if (!isAssignedType)
            // TODO 出错处理
            return null;

        return value;
    }
    public void setValue(String value) {
        if (!isAssignedType)
            // TODO 出错处理
            return;

        this.value = value;
    }
}
