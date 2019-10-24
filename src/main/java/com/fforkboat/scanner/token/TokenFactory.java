package com.fforkboat.scanner.token;

import java.util.List;

/**
 * 创建Token的静态工厂类。
 * 所有的Token类的构造函数都是包内可见的，所以在包外创建token只能通过该工厂。
 * 所有的工厂方法都能保证每个创建的token是合理的，如不会出现PointerToken的TokenType是TokenType.ADD的情况
 * */
public class TokenFactory {
    private static void checkTokenType(Class tokenClass, TokenType type){
        boolean isPointerToken = type == TokenType.IDENTIFIER || type == TokenType.DOUBLE_LITERAL || type == TokenType.INT_LITERAL || type == TokenType.STRING_LITERAL;

        if (type == TokenType.ARRAY_OPERATION || (tokenClass.equals(NormalToken.class) && isPointerToken) || (tokenClass.equals(PointerToken.class) && !isPointerToken))
            throw new IllegalArgumentException("Token type:" + type.name() + " should not be created by this method.");

    }

    public static NormalToken createNormalToken(TokenType type, int lineIndex){
        checkTokenType(NormalToken.class, type);

        return new NormalToken(type, lineIndex);
    }

    public static NormalToken createNormalToken(TokenType type, int lineIndex, ClusterFeature[] features){
        checkTokenType(NormalToken.class, type);

        return new NormalToken(type, lineIndex, features);
    }

    public static PointerToken createPointerToken(TokenType type, int lineIndex, int pointer){
        checkTokenType(PointerToken.class, type);

        return new PointerToken(type, lineIndex, pointer);
    }

    public static PointerToken createPointerToken(TokenType type, int lineIndex, ClusterFeature[] features, int pointer){
        checkTokenType(PointerToken.class, type);

        return new PointerToken(type, lineIndex, features, pointer);
    }

    public static ArrayOperationToken createArrayOperationToken(int lineIndex, int operationIndex) {
        return new ArrayOperationToken(lineIndex, operationIndex);
    }

    public static ArrayOperationToken createArrayOperationToken(int lineIndex, ClusterFeature[] features, int operationIndex) {
        return new ArrayOperationToken(lineIndex, features, operationIndex);
    }
}
