package com.fforkboat.scanner.token;

/**
 * 创建Token的静态工厂类。
 * 所有的Token类的构造函数都是包内可见的，所以在包外创建token只能通过该工厂。
 * 所有的工厂方法都能保证每个创建的token是合理的，如不会出现PointerToken的TokenType是TokenType.ADD的情况
 * */
public class TokenFactory {
    // 为创建normal token和 literal token检查参数token type是否合法
    private static void checkTokenType(Class tokenClass, TokenType type){
        boolean isLiteralToken = type == TokenType.BOOL_LITERAL || type == TokenType.DOUBLE_LITERAL || type == TokenType.INT_LITERAL || type == TokenType.STRING_LITERAL;

        if (type == TokenType.ARRAY_OPERATION || type == TokenType.IDENTIFIER || (tokenClass.equals(NormalToken.class) && isLiteralToken) || (tokenClass.equals(LiteralToken.class) && !isLiteralToken))
            throw new IllegalArgumentException("Token type:" + type.name() + " should not be created by this method.");

    }

    public static NormalToken createNormalToken(TokenType type, int lineIndex){
        checkTokenType(NormalToken.class, type);

        return new NormalToken(type, lineIndex);
    }

    public static LiteralToken createLiteralToken(TokenType type, int lineIndex, int pointer){
        checkTokenType(LiteralToken.class, type);

        return new LiteralToken(type, lineIndex, pointer);
    }

    public static IdentifierToken createIdentifierToken(int lineIndex, String identifierName){
        return new IdentifierToken(lineIndex, identifierName);
    }

    public static ArrayOperationToken createArrayOperationToken(int lineIndex, int operationIndex) {
        return new ArrayOperationToken(lineIndex, operationIndex);
    }
}
