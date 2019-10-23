package com.fforkboat.scanner;

import java.util.HashMap;
import java.util.Map;


/**
 * 创建Token的静态工厂类，通过该类可以创建所有种类的token
 * */
class TokenFactory {
    private static Map<TokenType, NormalToken> TokenPool = new HashMap<>();

    static NormalToken createNormalToken(TokenType type){
        if (type == TokenType.IDENTIFIER || type == TokenType.NUMBER || type == TokenType.SEMICOLON)
            throw new IllegalArgumentException("NormalToken type:" + type.name() + " should not be created by this method.");

        if (TokenPool.containsKey(type))
            return TokenPool.get(type);

        NormalToken token = new NormalToken(type);
        TokenPool.put(type, token);
        return token;
    }

    static PointerToken createPointerToken(TokenType type, int pointer){
        if (!(type == TokenType.IDENTIFIER || type == TokenType.NUMBER || type == TokenType.STRING_LITERAL))
            throw new IllegalArgumentException("The token type for token with pointer should be confined in " +
                    "TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.STRING_LITERAL");

        return new PointerToken(type, pointer);
    }

    static SemicolonToken createSemicolonToken(int position){
        return new SemicolonToken(position);
    }

    static ArrayOperationToken createArrayOperationToken(int index) {return new ArrayOperationToken(index);}
}
