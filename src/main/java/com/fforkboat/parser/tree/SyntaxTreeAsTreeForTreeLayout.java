package com.fforkboat.parser.tree;

import org.abego.treelayout.util.AbstractTreeForTreeLayout;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreeAsTreeForTreeLayout extends AbstractTreeForTreeLayout<SyntaxTreeNode> {
    public SyntaxTreeAsTreeForTreeLayout(SyntaxTreeNode root) {
        super(root);
    }

    @Override
    public SyntaxTreeNode getParent(SyntaxTreeNode syntaxTreeNode) {
        return syntaxTreeNode.getParent();
    }

    @Override
    public List<SyntaxTreeNode> getChildrenList(SyntaxTreeNode syntaxTreeNode) {
        if (syntaxTreeNode instanceof SyntaxTreeLeafNode)
            return new ArrayList<>();

        return ((SyntaxTreeBranchNode)syntaxTreeNode).getChildren();
    }
}
