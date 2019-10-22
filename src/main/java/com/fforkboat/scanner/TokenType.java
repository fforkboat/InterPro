package com.fforkboat.scanner;

public enum TokenType {
    IDENTIFIER,
    NUMBER,
    STRING_LITERAL,
    ARRAY_DECLARATION,

    IF,
    ELSE,
    WHILE,
    FOR,
    BREAK,
    CONTINUE,
    INT,
    DOUBLE,
    BOOL,
    STRING,
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
    LEFT_BRACKET,
    RIGHT_BRACKET,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,
    COMMA,

    QUOTATION
}
