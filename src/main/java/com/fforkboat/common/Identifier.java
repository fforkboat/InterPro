package com.fforkboat.common;

public class Identifier {
    private DataType dataType;
    private Object value;
    private String name;

    public Identifier(DataType type, String name){
        dataType = type;
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean setValue(Object value) {
        if(DataType.isDataTypeMatches(dataType, value.getClass()))
            return false;

        this.value = value;
        return true;
    }

}
