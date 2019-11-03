package com.fforkboat.parser.container;

import com.fforkboat.parser.tree.SyntaxTreeNode;

public class SyntaxTreeIfContainer extends SyntaxTreeContainer {
    private SyntaxTreeNode predicate;
    private SyntaxTreeNormalContainer elseConditionContainer;

    public SyntaxTreeIfContainer(SyntaxTreeContainer parent, SyntaxTreeNode predicate) {
        super(parent, false);
        this.predicate = predicate;
    }

    public SyntaxTreeNode getPredicate() {
        return predicate;
    }

    public SyntaxTreeNormalContainer getElseConditionContainer() {
        return elseConditionContainer;
    }

    public void setElseConditionContainer(SyntaxTreeNormalContainer elseConditionContainer) {
        this.elseConditionContainer = elseConditionContainer;
    }
}
