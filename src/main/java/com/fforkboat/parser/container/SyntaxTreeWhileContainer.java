package com.fforkboat.parser.container;

import com.fforkboat.parser.tree.SyntaxTreeNode;

/**
 * 表示一个while块的语法树容器
 * */
public class SyntaxTreeWhileContainer extends SyntaxTreeContainer {
    private SyntaxTreeNode predicate;

    public SyntaxTreeWhileContainer(SyntaxTreeContainer parent, SyntaxTreeNode predicate) {
        super(parent, false);
        this.predicate = predicate;
    }
}
