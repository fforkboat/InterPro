package com.fforkboat.scanner.token;

import com.fforkboat.parser.symbol.TerminalSymbolIndicator;

public enum TokenType implements TerminalSymbolIndicator {
    IDENTIFIER,
    ASSIGN,

    INT_LITERAL(new ClusterFeature[]{ClusterFeature.NUMBER}),
    REAL_LITERAL(new ClusterFeature[]{ClusterFeature.NUMBER}),
    STRING_LITERAL,
    BOOL_LITERAL,

    INT(new ClusterFeature[]{ClusterFeature.TYPE_DECLARATION}),
    REAL(new ClusterFeature[]{ClusterFeature.TYPE_DECLARATION}),
    STRING(new ClusterFeature[]{ClusterFeature.TYPE_DECLARATION}),
    BOOL(new ClusterFeature[]{ClusterFeature.TYPE_DECLARATION}),
    VOID,
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

    LEFT_PARENTHESIS, // (
    RIGHT_PARENTHESIS, // )
    LEFT_BRACE, // {
    RIGHT_BRACE, // }
    LEFT_BRACKET, // [
    RIGHT_BRACKET, //]
    SEMICOLON, //;
    COMMA,; //,

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
            case "<>":
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
    public static enum ClusterFeature implements TerminalSymbolIndicator {
        RELATIONAL_OPERATOR,
        TYPE_DECLARATION,
        NUMBER,
    }
}
