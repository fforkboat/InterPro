package com.fforkboat.parser.tree;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyntaxTreeBranchNode extends SyntaxTreeNode {
    private List<SyntaxTreeNode> children = new ArrayList<>();
    private String name;

    public SyntaxTreeBranchNode(String name, SyntaxTreeBranchNode parent) {
        super(parent);
        this.name = name;
    }

    public void addChild(SyntaxTreeNode node){
        children.add(node);
    }

    public List<SyntaxTreeNode> getChildren(){
        return children;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void reverseChildrenSequence(SyntaxTreeBranchNode root){
        Collections.reverse(root.children);

        for (SyntaxTreeNode node :
                root.children) {
            if (node instanceof SyntaxTreeBranchNode)
                reverseChildrenSequence((SyntaxTreeBranchNode) node);
        }
    }
}
