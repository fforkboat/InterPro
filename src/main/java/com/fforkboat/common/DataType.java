package com.fforkboat.common;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
    INT,
    REAL,
    BOOL,
    STRING,
    OBJECT,

    INT_ARRAY,
    REAL_ARRAY,
    BOOL_ARRAY,
    STRING_ARRAY;

    private static Map<DataType, Class> typeClassMap = new HashMap<>();
    static {
        typeClassMap.put(INT, Integer.class);
        typeClassMap.put(REAL, Double.class);
        typeClassMap.put(STRING, String.class);
        typeClassMap.put(BOOL, Boolean.class);
        typeClassMap.put(INT_ARRAY, Integer[].class);
        typeClassMap.put(REAL_ARRAY, Double[].class);
        typeClassMap.put(BOOL_ARRAY, String[].class);
        typeClassMap.put(STRING_ARRAY, Boolean[].class);
    }

    public static boolean isDataTypeMatches(DataType dataType, Class cs){
        if (dataType == OBJECT)
            return true;

        Class cls = typeClassMap.get(dataType);
        return cls == cs;
    }
}