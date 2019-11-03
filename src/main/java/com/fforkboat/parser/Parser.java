package com.fforkboat.parser;

import com.fforkboat.common.CompileError;
import com.fforkboat.parser.container.SyntaxTreeContainer;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;
import com.fforkboat.parser.symbol.*;
import com.fforkboat.parser.tree.SyntaxTreeBranchNode;
import com.fforkboat.parser.tree.SyntaxTreeLeafNode;
import com.fforkboat.scanner.Scanner;
import com.fforkboat.scanner.token.Token;
import com.fforkboat.scanner.token.TokenType;
import j2html.tags.ContainerTag;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws IOException {
        List<CompileError> errors = new ArrayList<>();
        List<Token> tokens = Scanner.scan(new File("src/test/input/b.txt"));
        ParserContext context = ParserContext.getInstance();

        SyntaxTreeNormalContainer rootContainer = new SyntaxTreeNormalContainer(null, true);
        context.setRootContainer(rootContainer);
        context.setCurrentSyntaxTreeContainer(rootContainer);
        context.reset();

        int i = 0;
        while (i < tokens.size()){
            Token token = tokens.get(i);

            if (token.getTokenType() == TokenType.RIGHT_BRACE && !context.isRightBraceForArray()){
                SyntaxTreeContainer parent = context.getCurrentSyntaxTreeContainer().getParent();
                if (parent == null){
                    // TODO 出错处理
                    errors.add(new CompileError("Parser: unmatched right brace", token.getLineIndexOfSourceProgram()));
                }
                else{
                    context.setCurrentSyntaxTreeContainer(parent);
                }

                i++;
                continue;
            }

            if (context.getSymbolStack().size() == 0){
                // TODO 出错处理
                errors.add(new CompileError("Parser: Something wrong", token.getLineIndexOfSourceProgram()));
                break;
            }

            Symbol symbol = context.getSymbolStack().pop();
            if (symbol instanceof NonterminalSymbol){
                if (!((NonterminalSymbol)symbol).canReceiveToken(token)){
                    errors.add(new CompileError("Parser: Something wrong", token.getLineIndexOfSourceProgram()));
                    break;
                }

                List<Symbol> right = ((NonterminalSymbol) symbol).getRightPart(token);
                if (context.isShouldContinue()){
                    context.setShouldContinue(false);
                    i++;
                    continue;
                }
          
                SyntaxTreeBranchNode branchNode = (SyntaxTreeBranchNode) context.getNodeStack().pop();
                for (int j = right.size() - 1; j >= 0; j--) {
                    Symbol sym = right.get(j);
                    context.getSymbolStack().push(sym);

                    if (sym instanceof NonterminalSymbol){
                        SyntaxTreeBranchNode branch = new SyntaxTreeBranchNode(((NonterminalSymbol) sym).getSymbolName(), branchNode);
                        branchNode.addChild(branch);
                        context.getNodeStack().push(branch);
                    }
                    else {
                        SyntaxTreeLeafNode leaf = new SyntaxTreeLeafNode(branchNode);
                        branchNode.addChild(leaf);
                        context.getNodeStack().push(leaf);
                    }
                }
            }
            else {
                TerminalSymbolIndicator indicator = ((TerminalSymbol) symbol).getIndicator();

                boolean isMatch = false;
                if (token.getTokenType() == indicator){
                    isMatch = true;
                }
                else{
                    for (TokenType.ClusterFeature feature :
                            token.getTokenType().getClusterFeatures()) {
                        if (feature == indicator) {
                            isMatch = true;
                            break;
                        }
                    }
                }

                if (isMatch){
                    SyntaxTreeLeafNode leaf = (SyntaxTreeLeafNode) context.getNodeStack().pop();
                    leaf.setToken(token);

                    if (token.getTokenType() == TokenType.SEMICOLON){
                        context.addSyntaxTreeToContainer();
                    }
                    if (token.getTokenType() == TokenType.RIGHT_BRACE){
                        context.setRightBraceForArray(false);
                    }

                    i++;
                }
                else{
                    //TODO 出错处理
                    errors.add(new CompileError("Parser: Something wrong", token.getLineIndexOfSourceProgram()));
                }
            }
        }

        for (Pair<ContainerTag, Integer> pair :
                context.getAllHtmlContainers()) {
            try(FileWriter writer = new FileWriter("output/" + pair.getValue1() + ".html")){
                writer.write(pair.getValue0().render());
            }
        }
        
        if (errors.size() > 0){
            System.out.println(errors);
        }
    }
}
