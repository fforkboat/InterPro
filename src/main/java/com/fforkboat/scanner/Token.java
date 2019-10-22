package com.fforkboat.scanner;

abstract class Token {
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
