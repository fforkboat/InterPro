package com.fforkboat.parser.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 语法树的分支节点
 * */
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

    public int getLineIndexOfSourceProgramOfFirstToken(){
        for (SyntaxTreeNode child :
                children) {
            if (child instanceof SyntaxTreeLeafNode){
                return ((SyntaxTreeLeafNode) child).getToken().getLineIndexOfSourceProgram();
            }
            else {
                int lineIndex = ((SyntaxTreeBranchNode)child).getLineIndexOfSourceProgramOfFirstToken();
                if (lineIndex != -1)
                    return lineIndex;
            }
        }

        return -1;
    }


    // 添加孩子节点时是逆序进行的(因为是在符号入栈过程中添加的孩子，而符号入栈需要逆序进行)，所以在最后得到的语法树中，
    // 分支节点的孩子节点的顺序与产生式右部中符号的顺序是反的，为了看着方便，提供这个方法调整孩子节点的顺序
    public static void reverseChildrenSequence(SyntaxTreeBranchNode root){
        Collections.reverse(root.children);

        for (SyntaxTreeNode node :
                root.children) {
            if (node instanceof SyntaxTreeBranchNode)
                reverseChildrenSequence((SyntaxTreeBranchNode) node);
        }
    }
}
