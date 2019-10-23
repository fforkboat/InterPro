package com.fforkboat.scanner.token;


/**
 * 所有token类的抽象基类
 * */
public abstract class Token {
    private TokenType tokenType;
    protected Token(TokenType tokenType){
        this.tokenType = tokenType;
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    @Override
    public String toString() {
        return "<" + tokenType.name() + ">";
    }
}
