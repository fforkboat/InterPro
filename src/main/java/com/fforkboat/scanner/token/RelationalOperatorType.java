package com.fforkboat.scanner.token;

public enum RelationalOperatorType {
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    EQUAL,
    UNEQUAL,;

    public static RelationalOperatorType getInstance(String arg){
        switch (arg){
            case ">":
                return GREATER;
            case ">=":
                return GREATER_EQUAL;
            case "<":
                return LESS;
            case "<=":
                return LESS_EQUAL;
            case "==":
                return EQUAL;
            case "!=":
                return UNEQUAL;
            default:
                return null;
        }
    }
}
