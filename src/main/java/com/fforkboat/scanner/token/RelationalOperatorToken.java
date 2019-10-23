package com.fforkboat.scanner.token;

public class RelationalOperatorToken extends Token {
    private RelationalOperatorType relationalOperatorType;

    RelationalOperatorToken(RelationalOperatorType type){
        super(TokenType.RELATIONAL_OPERATOR);

        this.relationalOperatorType = type;
    }

    public RelationalOperatorType getRelationalOperatorType() {
        return relationalOperatorType;
    }

    @Override
    public String toString() {
        return "<" + getTokenType() + ", " + relationalOperatorType.name() + ">";
    }
}
