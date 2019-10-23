package com.fforkboat.scanner.token;

import com.fforkboat.common.VariableType;

public class VariableDeclarationToken extends Token {
    private VariableType variableType;

    VariableDeclarationToken(VariableType variableType) {
        super(TokenType.VARIABLE_DECLARATION);

        this.variableType = variableType;
    }

    public VariableType getVariableType(){
        return variableType;
    }

    @Override
    public String toString() {
        return "<" + getTokenType().name() + ", " + variableType.name() + ">";
    }
}
