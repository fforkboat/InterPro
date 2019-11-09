package com.fforkboat.scanner.token;


/**
 * 最普通的token
 * 这样的token只需要知道自己的类型，不需要额外的信息
 * */
public class NormalToken extends Token {
    NormalToken(TokenType type, int lineIndex){
        super(type, lineIndex);
    }
}

