package com.fforkboat.common;

public class Identifier {
    private DataType dataType;
    private String value;

    public Identifier(DataType type){
        dataType = type;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getValue() {
        if (value == null){
            // TODO: 报错
        }
        return value;
    }
    public void setValue(String value) {
        // TODO 检查
        this.value = value;
    }
}
