package com.fforkboat.parser.tree;

import com.fforkboat.parser.symbol.TerminalSymbol;
import com.fforkboat.scanner.token.Token;
import org.graphstream.graph.implementations.SingleGraph;

public class SyntaxTreeLeafNode extends SyntaxTreeNode {
    private Token token;

    public SyntaxTreeLeafNode(SyntaxTreeBranchNode parent) {
        super(parent);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        if (token == null)
            return "Not assigned leaf node";

        return token.getTokenType().name();
    }
}
