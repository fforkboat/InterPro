package com.fforkboat.parser;

import com.fforkboat.scanner.token.Token;

public interface ProductionHandler {
    void handle(Token token);
}
