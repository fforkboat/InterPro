package com.fforkboat.parser;

import com.fforkboat.scanner.token.Token;

/**
 * 此接口为函数式接口(functional interface)
 * 代表了产生式进行推导(从非终结符得到其右部的过程)时要执行的操作，如向函数表中添加函数、记录标识符的类型
 * */
public interface ProductionHandler {
    void handle(Token token);
}
