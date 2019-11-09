package com.fforkboat.common;

import java.util.Collections;
import java.util.List;

public abstract class BaseFunction {
    private String name;
    private ReturnType returnType;
    private List<DataType> argumentTypes;

    BaseFunction(String name, ReturnType returnType, List<DataType> argumentTypes) {
        this.name = name;
        this.returnType = returnType;
        this.argumentTypes = Collections.unmodifiableList(argumentTypes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public List<DataType> getArgumentTypes() {
        return argumentTypes;
    }
}
