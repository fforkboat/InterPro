package com.fforkboat.scanner;

public class SemicolonToken extends NormalToken {
    // 指令的位置，即指令在哪一行
    private int instructionPosition;

    SemicolonToken(int instructionPosition){
        super(TokenType.SEMICOLON);

        this.instructionPosition = instructionPosition;
    }

    public int getPosition() {
        return instructionPosition;
    }

    @Override
    public String toString() {
        return "<" + getTokenType().name() + ", " + instructionPosition + ">";
    }
}
