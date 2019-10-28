package com.fforkboat.scanner.token;

public enum TokenType {
    IDENTIFIER,
    ARRAY_DECLARATION,
    ASSIGN,
    ARRAY_OPERATION,

    INT_LITERAL,
    DOUBLE_LITERAL,
    STRING_LITERAL,
    BOOL_LITERAL,

    INT(new ClusterFeature[]{ClusterFeature.NUMBER, ClusterFeature.VARIABLE_DECLARATION, ClusterFeature.ARGUMENT_TYPE, ClusterFeature.RETURN_TYPE}),
    DOUBLE(new ClusterFeature[]{ClusterFeature.NUMBER, ClusterFeature.VARIABLE_DECLARATION, ClusterFeature.ARGUMENT_TYPE, ClusterFeature.RETURN_TYPE}),
    STRING(new ClusterFeature[]{ClusterFeature.VARIABLE_DECLARATION, ClusterFeature.ARGUMENT_TYPE, ClusterFeature.RETURN_TYPE}),
    BOOL(new ClusterFeature[]{ClusterFeature.VARIABLE_DECLARATION, ClusterFeature.ARGUMENT_TYPE, ClusterFeature.RETURN_TYPE}),
    VOID(new ClusterFeature[]{ClusterFeature.RETURN_TYPE}),
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

    LESS(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),
    LESS_EQUAL(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),
    GREATER(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),
    GREATER_EQUAL(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),
    EQUAL(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),
    UNEQUAL(new ClusterFeature[]{ClusterFeature.RELATIONAL_OPERATOR}),

    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,
    COMMA,;

    private ClusterFeature[] clusterFeatures;

    TokenType(ClusterFeature[] features){
        this.clusterFeatures = features;
    }

    TokenType(){
        this(new ClusterFeature[0]);
    }

    public ClusterFeature[] getClusterFeatures(){
        return clusterFeatures;
    }

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

    /**
     * TokenType可能拥有的聚类特性。
     * 某些token在语法分析过程中有相似的特性。比如 <LESS> <LESS_EQUAL> 这类表示关系的token，在语法分析器看来是一样的
     * 为token设置聚类特性可以减少语法分析中文法的产生式
     * */
    public enum ClusterFeature {
        RELATIONAL_OPERATOR,
        VARIABLE_DECLARATION,
        NUMBER,
        ARGUMENT_TYPE,
        RETURN_TYPE
    }
}
