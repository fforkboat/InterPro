package com.fforkboat.parser;

import com.fforkboat.Configure;
import com.fforkboat.common.Error;
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
    /**
     * @param tokens the token sequence
     * @return the root syntax tree container. The result is null if there are errors in the source code file.
     * */
    public static SyntaxTreeNormalContainer parse(List<Token> tokens) throws IOException {
        ParserContext context = ParserContext.getInstance();

        // 创建根语法树容器(全局语法树容器),初始化Parser的上下文，准备开始语法分析
        SyntaxTreeNormalContainer rootContainer = new SyntaxTreeNormalContainer(null, true);
        context.setRootContainer(rootContainer);
        context.setCurrentSyntaxTreeContainer(rootContainer);
        context.reset();

        // 从头开始读取token
        int i = 0;

        Token token = null;
        while (i < tokens.size()){
            token = tokens.get(i);

            // 当读到的token是}时，并且该}不是一个数组定义的}，那这个}就代表了一个语法块的结束
            // 此时需要把“当前语法树容器”的父容器设置为新的“当前语法树容器”
            // 因为这个符号不能从产生式中推出，所以不能在ProductionHandler中处理，只能放在这里
            if (token.getTokenType() == TokenType.RIGHT_BRACE && !context.isRightBraceForArray()){
                SyntaxTreeContainer parent = context.getCurrentSyntaxTreeContainer().getParent();
                if (parent == null){
                    // TODO 出错处理
                    context.getErrors().add(Error.createCompileError("Parser: unmatched right brace.", token.getLineIndexOfSourceProgram()));
                    i = continueFromNextLine(tokens, context, i, token);
                    continue;
                }
                else{
                    if (context.isInImplicitBlock()) {
                        context.setCurrentSyntaxTreeContainer(parent.getParent());
                        context.setInImplicitBlock(false);
                    }
                    else
                        context.setCurrentSyntaxTreeContainer(parent);

                    i++;
                    continue;
                }
            }

            // 当符号栈中已经没有符号但是token还没有读完时，说明出现语法错误
            if (context.getSymbolStack().size() == 0){
                // TODO 出错处理
                context.getErrors().add(Error.createCompileError("Parser: Something wrong.", token.getLineIndexOfSourceProgram()));

                i = continueFromNextLine(tokens, context, i, token);
                continue;
            }

            // 从符号栈中取出一个符号，判断它是终结符还是非终结符，并进行相应的处理
            Symbol symbol = context.getSymbolStack().pop();

            // 非终结符的情况
            if (symbol instanceof NonterminalSymbol){
                if (!((NonterminalSymbol)symbol).canReceiveToken(token)){
                    context.getErrors().add(Error.createCompileError("Parser: Unexpected token.", token.getLineIndexOfSourceProgram()));

                    i = continueFromNextLine(tokens, context, i, token);
                    continue;
                }

                List<Symbol> right = ((NonterminalSymbol) symbol).getRightPart(token);
                // 在获取右部的过程中(getRightPart)，会调用ProductionHandler对象的handle函数
                // 如果进入了新的语法块，在handle函数中会改变当前语法树节点和语法树容器，需要开始进行新一轮的token分析
                // 此时本轮分析不再需要进行，直接退出
                if (context.isContinueWithNextToken()){
                    context.setContinueWithNextToken(false);
                    i++;
                    continue;
                }
                if (context.isContinueWithThisToken()){
                    context.setContinueWithThisToken(false);
                    continue;
                }
                // 如果在handler函数中检测到错误，从下一行再开始
                if (context.isErrorInProductionHandler()){
                    context.setErrorInProductionHandler(false);
                    i = continueFromNextLine(tokens, context, i, token);
                    continue;
                }

                SyntaxTreeBranchNode branchNode = (SyntaxTreeBranchNode) context.getNodeStack().pop();

                // 将产生式右部入栈
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
            // 终结符的情况
            else {
                TerminalSymbolIndicator indicator = ((TerminalSymbol) symbol).getIndicator();

                // 判断当前token是否能被该非终结符识别
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

                    // 当前token被识别时，指针移到下一个token
                    i++;
                }
                else{
                    context.getErrors().add(Error.createCompileError("Parser: Unexpected token.", token.getLineIndexOfSourceProgram()));

                    continueFromNextLine(tokens, context, i, token);
                }
            }
        }

        // 判断符号栈中是否还有多余的符号
        if (token != null && context.getSymbolStack().size() != 0){
            Symbol symbol = context.getSymbolStack().pop();
            if (symbol instanceof TerminalSymbol || !((NonterminalSymbol) symbol).getSymbolName().equals("S")){
                context.getErrors().add(Error.createCompileError("Parser: Existing unmatched symbols in the symbol stack", token.getLineIndexOfSourceProgram()));
            }
        }

        // 判断是否缺少}符号
        if (token != null && context.getCurrentSyntaxTreeContainer() != rootContainer){
            context.getErrors().add(Error.createCompileError("Parser: Missing } token.", token.getLineIndexOfSourceProgram()));
        }

        if (context.getErrors().size() != 0){
            Error.printErrorList(context.getErrors());
            return null;
        }

        if (Configure.isPrintSyntaxTrees){
            for (Pair<ContainerTag, Integer> pair :
                    context.getAllHtmlContainers()) {
                try(FileWriter writer = new FileWriter("SyntaxTreeContainer-" + pair.getValue1() + ".html")){
                    writer.write(pair.getValue0().render());
                }
            }
        }


        return rootContainer;
    }

    // 从下一行开始继续尝试识别
    private static int continueFromNextLine(List<Token> tokens, ParserContext context, int i, Token token) {
        if (i == tokens.size() - 1)
            return tokens.size();

        int currentLine = token.getLineIndexOfSourceProgram();
        token = tokens.get(++i);
        while (token.getLineIndexOfSourceProgram() == currentLine){
            if (i == tokens.size() - 1)
                return tokens.size();
            token = tokens.get(++i);
        }

        context.reset();
        return i;
    }
}
