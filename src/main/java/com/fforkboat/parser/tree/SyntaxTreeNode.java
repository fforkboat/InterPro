package com.fforkboat.parser.tree;

import com.fforkboat.parser.container.SyntaxTreeContainerComponent;

/**
 * 语法树节点的基类
 * */
public abstract class SyntaxTreeNode implements SyntaxTreeContainerComponent {
    private SyntaxTreeBranchNode parent;

    public SyntaxTreeNode(SyntaxTreeBranchNode parent) {
        this.parent = parent;
    }

    public SyntaxTreeBranchNode getParent() {
        return parent;
    }
}
