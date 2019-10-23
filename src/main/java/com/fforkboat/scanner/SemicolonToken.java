package com.fforkboat.scanner;


/**
 * 字符;的token，用于分割指令
 * 该token带有一个行号信息，表示指令在源程序中的行号，用于在解释执行过程中报错的定位
 * */
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
