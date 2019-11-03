package com.fforkboat.scanner.token;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 所有token类的抽象基类
 * 该类是不可变的
 * */
public abstract class Token {
    // token的类型
    private TokenType tokenType;
    // 这个token在源程序中的行数
    private int lineIndexOfSourceProgram;
    // 这个token可能具有的用于聚类的特性

    protected Token(TokenType tokenType, int lineIndex){
        this.tokenType = tokenType;
        this.lineIndexOfSourceProgram = lineIndex;
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    public int getLineIndexOfSourceProgram() {
        return lineIndexOfSourceProgram;
    }

    @Override
    public String toString() {
        return "<" + tokenType.name() + ">";
    }
}
