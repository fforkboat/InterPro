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
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
