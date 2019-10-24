package com.fforkboat.scanner.token;

public enum TokenType {
    IDENTIFIER,
    INT_LITERAL,
    DOUBLE_LITERAL,
    NUMBER_LITERAL,

    STRING_LITERAL,
    ARRAY_DECLARATION,

    INT,
    DOUBLE,
    STRING,
    BOOL,

    VARIABLE_DECLARATION,

    IF,
    ELSE,
    WHILE,
    FOR,
    BREAK,
    CONTINUE,
    TRUE,
    FALSE,
    FUNCTION,
    RETURN,

    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    EQUAL,
    UNEQUAL,
    ASSIGN,
    ARRAY_OPERATION,

    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,
    COMMA,;

    public static TokenType getTypeForRelationalOperator(String operator){
        switch (operator){
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
