package com.fforkboat.parser.container;

import com.fforkboat.parser.tree.SyntaxTreeBranchNode;
import com.fforkboat.parser.tree.SyntaxTreeNode;


/**
 * 表示一个if块的语法树容器
 * */
public class SyntaxTreeIfContainer extends SyntaxTreeContainer {
    private SyntaxTreeBranchNode predicate;
    private SyntaxTreeNormalContainer elseConditionContainer;

    public SyntaxTreeIfContainer(SyntaxTreeContainer parent, SyntaxTreeBranchNode predicate) {
        super(parent, false);
        this.predicate = predicate;
    }

    public SyntaxTreeBranchNode getPredicate() {
        return predicate;
    }

    public SyntaxTreeNormalContainer getElseConditionContainer() {
        return elseConditionContainer;
    }

    public void setElseConditionContainer(SyntaxTreeNormalContainer elseConditionContainer) {
        this.elseConditionContainer = elseConditionContainer;
    }
}
