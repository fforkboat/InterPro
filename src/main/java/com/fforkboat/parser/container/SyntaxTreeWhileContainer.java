package com.fforkboat.parser.container;

import com.fforkboat.parser.tree.SyntaxTreeNode;

public class SyntaxTreeWhileContainer extends SyntaxTreeContainer {
    private SyntaxTreeNode predicate;

    public SyntaxTreeWhileContainer(SyntaxTreeContainer parent, SyntaxTreeNode predicate) {
        super(parent, false);
        this.predicate = predicate;
    }
}
