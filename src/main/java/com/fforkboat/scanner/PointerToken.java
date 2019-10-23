package com.fforkboat.scanner;

/**
 * 这类token带有指向符号表的指针
 * 这类token的TokenType可以是identifier, number, string literal
 * */
public class PointerToken extends Token {
    private int pointer;

    PointerToken(TokenType type, int pointer){
        super(type);
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
