package com.fforkboat.parser.tree;

import com.fforkboat.parser.container.SyntaxTreeContainerComponent;

public abstract class SyntaxTreeNode implements SyntaxTreeContainerComponent {
    private SyntaxTreeBranchNode parent;

    public SyntaxTreeNode(SyntaxTreeBranchNode parent) {
        this.parent = parent;
    }

    public SyntaxTreeBranchNode getParent() {
        return parent;
    }
}
