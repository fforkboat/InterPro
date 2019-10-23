package com.fforkboat.scanner.token;

import java.util.HashMap;
import java.util.Map;

import com.fforkboat.common.VariableType;

/**
 * 创建Token的静态工厂类。
 * 所有的Token类的构造函数都是包内可见的，所以在包外创建token只能通过该工厂。
 * 所有的工厂方法都能保证每个创建的token是合理的，如不会出现PointerToken的TokenType是TokenType.ADD的情况
 * */
public class TokenFactory {
    // NormalToken的缓存池。Token类是不可变的，可以随意复用。
    // TODO： 其他类型的token其实也可以缓存，但是会有点麻烦，以后有空可以实现看看
    private static Map<TokenType, NormalToken> TokenPool = new HashMap<>();

    public static NormalToken createNormalToken(TokenType type){
        if (type == TokenType.IDENTIFIER || type == TokenType.NUMBER || type == TokenType.SEMICOLON)
            throw new IllegalArgumentException("NormalToken type:" + type.name() + " should not be created by this method.");

        if (TokenPool.containsKey(type))
            return TokenPool.get(type);

        NormalToken token = new NormalToken(type);
        TokenPool.put(type, token);
        return token;
    }

    public static PointerToken createPointerToken(TokenType type, int pointer){
        if (!(type == TokenType.IDENTIFIER || type == TokenType.NUMBER || type == TokenType.STRING_LITERAL))
            throw new IllegalArgumentException("The token type for token with pointer should be confined in " +
                    "TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.STRING_LITERAL");

        return new PointerToken(type, pointer);
    }

    public static SemicolonToken createSemicolonToken(int position){
        return new SemicolonToken(position);
    }

    public static ArrayOperationToken createArrayOperationToken(int index) {return new ArrayOperationToken(index);}

    public static VariableDeclarationToken createVariableDeclarationToken(VariableType type){
        return new VariableDeclarationToken(type);
    }

    public static RelationalOperatorToken createRelationalOperatorToken(RelationalOperatorType type){
        return new RelationalOperatorToken(type);
    }
}
