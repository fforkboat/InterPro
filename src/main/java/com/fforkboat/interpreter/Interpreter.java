package com.fforkboat.interpreter;

import com.fforkboat.Configure;
import com.fforkboat.common.Error;
import com.fforkboat.parser.Parser;
import com.fforkboat.parser.container.SyntaxTreeContainer;
import com.fforkboat.parser.container.SyntaxTreeContainerComponent;
import com.fforkboat.parser.container.SyntaxTreeIfContainer;
import com.fforkboat.parser.container.SyntaxTreeWhileContainer;
import com.fforkboat.parser.tree.SyntaxTreeBranchNode;
import com.fforkboat.parser.tree.SyntaxTreeNode;
import com.fforkboat.scanner.Scanner;
import com.fforkboat.scanner.token.Token;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.*;

public class Interpreter {
    public static void main(String[] args){
        Options options = new Options();

        Option sourceFile = new Option("f", "input", true, "source file path");
        sourceFile.setRequired(true);
        options.addOption(sourceFile);

        Option printTokens = new Option("t", "print_tokens", false, "whether to print tokens");
        printTokens.setRequired(false);
        options.addOption(printTokens);

        Option printSyntaxTrees = new Option("s", "generate_trees", false, "whether to generate SVGs for syntax trees");
        printSyntaxTrees.setRequired(false);
        options.addOption(printSyntaxTrees);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("InterPro", options);

            return;
        }

        if (cmd.hasOption("t")){
            Configure.isPrintTokens = true;
        }
        if (cmd.hasOption("s")){
            Configure.isPrintSyntaxTrees = true;
        }

        List<Token> tokens;
        SyntaxTreeContainer rootContainer;
        try {
            tokens = Scanner.scan(new File(cmd.getOptionValue("f")));
            if (tokens == null)
                return;
        }catch (IOException e){
            System.out.println("Can not open source file.");
            return;
        }

        try {
            rootContainer = Parser.parse(tokens);
            if (rootContainer == null){
                return;
            }
        }catch (IOException e){
            System.out.println("Can not generate syntax tree SVGs.");
            return;
        }

        InterpreterContext.setCurrentContainer(rootContainer);
        interpret(rootContainer);
    }

    public static void interpret(SyntaxTreeContainer rootContainer){
        for (SyntaxTreeContainerComponent component:
             rootContainer.getComponents()) {
            if (!InterpreterContext.isContinueToExecute())
                return;

            // 如果是语法树，直接执行
            if (component instanceof SyntaxTreeNode){
                BranchNodeAction action = BranchNodeActionProvider.getAction("S");
                action.act(((SyntaxTreeBranchNode)component).getChildren());
                if (!InterpreterContext.isContinueToExecute())
                    return;
            }
            // 如果是子语法树容器，递归执行子语法树容器
            else {
                SyntaxTreeContainer nextContainer = ((SyntaxTreeContainer)component);
                SyntaxTreeContainer currentContainer = InterpreterContext.getCurrentContainer();

                // while语法树容器的情况
                if (nextContainer instanceof SyntaxTreeWhileContainer){
                    SyntaxTreeWhileContainer whileContainer = (SyntaxTreeWhileContainer) nextContainer;
                    InterpreterContext.setCurrentContainer(nextContainer);
                    Boolean result = isPredicateTrue(whileContainer.getPredicate());
                    InterpreterContext.setCurrentContainer(currentContainer);
                    if (result == null)
                        return;

                    while (result){
                        InterpreterContext.setCurrentContainer(nextContainer);
                        interpret(nextContainer);
                        if (InterpreterContext.isEncounterContinue()){
                            InterpreterContext.setContinueToExecute(true);
                            InterpreterContext.setEncounterContinue(false);
                        }
                        else if (InterpreterContext.isEncounterBreak()){
                            InterpreterContext.setContinueToExecute(true);
                            InterpreterContext.setEncounterBreak(false);
                            break;
                        }
                        // 遇见错误了
                        else if (!InterpreterContext.isContinueToExecute()){
                            return;
                        }
                        result = isPredicateTrue(whileContainer.getPredicate());
                        if (result == null)
                            return;
                        InterpreterContext.setCurrentContainer(currentContainer);
                    }
                }
                else if (nextContainer instanceof SyntaxTreeIfContainer){
                    SyntaxTreeIfContainer ifContainer = (SyntaxTreeIfContainer) nextContainer;
                    InterpreterContext.setCurrentContainer(nextContainer);
                    Boolean result = isPredicateTrue(ifContainer.getPredicate());
                    InterpreterContext.setCurrentContainer(currentContainer);
                    if (result == null)
                        return;

                    if (!result){
                        if (ifContainer.getElseConditionContainer() != null){
                            InterpreterContext.setCurrentContainer(ifContainer.getElseConditionContainer());
                            interpret(ifContainer.getElseConditionContainer());
                            InterpreterContext.setCurrentContainer(currentContainer);
                        }
                    }
                    else {
                        InterpreterContext.setCurrentContainer(nextContainer);
                        interpret(nextContainer);
                        InterpreterContext.setCurrentContainer(currentContainer);
                    }
                }
                else{
                    InterpreterContext.setCurrentContainer(nextContainer);
                    interpret(nextContainer);
                    InterpreterContext.setCurrentContainer(currentContainer);
                }
            }
        }
    }

    static Object interpretFunctionWithReturnValue(SyntaxTreeContainer function, int lineCount){
        for (SyntaxTreeContainerComponent component:
                function.getComponents()) {
            if (component instanceof SyntaxTreeNode){
                SyntaxTreeBranchNode rootNode = (SyntaxTreeBranchNode)component;
                BranchNodeAction action = BranchNodeActionProvider.getAction("S");

                Object result = action.act(rootNode.getChildren());
                if (!InterpreterContext.isContinueToExecute())
                    return null;

                if (result != null)
                    return result;
            }
            else {
                SyntaxTreeContainer currentContainer = InterpreterContext.getCurrentContainer();
                InterpreterContext.setCurrentContainer(((SyntaxTreeContainer)component));
                interpret(((SyntaxTreeContainer)component));
                InterpreterContext.setCurrentContainer(currentContainer);
            }
        }

        InterpreterContext.throwError(Error.createRuntimeError("No return clause found.", lineCount));
        return null;
    }

    private static Boolean isPredicateTrue(SyntaxTreeBranchNode predicate){
        SyntaxTreeBranchNode D = (SyntaxTreeBranchNode) predicate.getChildren().get(1);
        SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) D.getChildren().get(1);

        Object result = BranchNodeActionProvider.getAction("W1").act(W1.getChildren());
        if (result == null)
            return null;
        if (result instanceof Integer){
            return (Integer)result != 0;
        }
        if (result instanceof Double){
            return (Double)result != 0;
        }
        if (result instanceof Boolean){
            return (Boolean) result;
        }


        InterpreterContext.throwError(
                Error.createRuntimeError("Expression is not a bool value", predicate.getLineIndexOfSourceProgramOfFirstToken())
        );
        return null;
    }

}