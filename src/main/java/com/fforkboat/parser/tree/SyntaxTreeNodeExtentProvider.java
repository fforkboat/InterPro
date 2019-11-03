package com.fforkboat.parser.tree;

import org.abego.treelayout.NodeExtentProvider;

public class SyntaxTreeNodeExtentProvider implements NodeExtentProvider<SyntaxTreeNode> {

    @Override
    public double getWidth(SyntaxTreeNode syntaxTreeNode) {
        return syntaxTreeNode.toString().length() * 10 + 15;
    }

    @Override
    public double getHeight(SyntaxTreeNode syntaxTreeNode) {
        return 20;
    }
}
