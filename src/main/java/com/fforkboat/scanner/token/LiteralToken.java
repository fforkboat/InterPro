package com.fforkboat.scanner.token;

/**
 * 字面量token
 * 这类token带有指向符号表的指针
 * 这类token的TokenType可以是number, string literal, bool literal
 * */
public class LiteralToken extends Token {
    private int pointer;

    LiteralToken(TokenType type, int lineIndex, int pointer){
        super(type, lineIndex);
        this.pointer = pointer;
    }

    public int getPointer() {
        return pointer;
    }

    @Override
    public String toString() {
        return "<" + getTokenType().name() + ", " + pointer + ">";
    }
}
