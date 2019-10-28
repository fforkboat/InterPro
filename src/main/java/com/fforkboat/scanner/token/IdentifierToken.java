package com.fforkboat.scanner.token;

public class IdentifierToken extends Token {
    private String identifierName;

    IdentifierToken(int lineIndex, String identifierName) {
        super(TokenType.IDENTIFIER, lineIndex);
        this.identifierName = identifierName;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    @Override
    public String toString() {
        return "<" + getTokenType().name() + ", " + identifierName + ">";
    }
}
