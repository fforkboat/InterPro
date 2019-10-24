package com.fforkboat.scanner.token;


import java.util.List;

/**
 * 最普通的token，这样的token只需要知道自己的类型，不需要额外的信息
 * */
public class NormalToken extends Token {
    NormalToken(TokenType type, int lineIndex){
        super(type, lineIndex);
    }

    NormalToken(TokenType type, int lineIndex, ClusterFeature[] features){
        super(type, lineIndex, features);
    }
}

