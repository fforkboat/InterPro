package com.fforkboat.scanner.token;


import java.util.List;

/**
 * 数组取值操作([operationIndex])的token
 * 该类token保存了index的值，在解释执行时使用
 * */
public class ArrayOperationToken extends Token {
    private int operationIndex;

    ArrayOperationToken(int lineIndex, int operationIndex) {
        super(TokenType.ARRAY_OPERATION, lineIndex);

        this.operationIndex = operationIndex;
    }

    ArrayOperationToken(int lineIndex, ClusterFeature[] features, int operationIndex) {
        super(TokenType.ARRAY_OPERATION, lineIndex, features);

        this.operationIndex = operationIndex;
    }

    public int getOperationIndex() {
        return operationIndex;
    }
}
