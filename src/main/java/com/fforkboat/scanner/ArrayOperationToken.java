package com.fforkboat.scanner;


/**
 * 数组取值操作([index])的token
 * 该类token保存了index的值，用于解释执行
 * */
public class ArrayOperationToken extends Token {
    private int index;

    ArrayOperationToken(int index) {
        super(TokenType.ARRAY_OPERATION);

        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
