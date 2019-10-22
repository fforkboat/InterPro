package com.fforkboat.scanner;

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
