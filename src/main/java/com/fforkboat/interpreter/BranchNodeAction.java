package com.fforkboat.interpreter;

import com.fforkboat.parser.tree.SyntaxTreeNode;

import java.util.List;

interface BranchNodeAction {
    Object act(List<SyntaxTreeNode> children);
}
