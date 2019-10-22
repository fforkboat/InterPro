package com.fforkboat.scanner;

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
