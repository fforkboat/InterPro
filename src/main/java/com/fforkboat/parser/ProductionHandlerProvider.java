package com.fforkboat.parser;

import com.fforkboat.common.CustomFunction;
import com.fforkboat.common.Error;
import com.fforkboat.common.DataType;
import com.fforkboat.common.ReturnType;
import com.fforkboat.parser.container.*;
import com.fforkboat.parser.tree.SyntaxTreeBranchNode;
import com.fforkboat.program.FunctionTable;
import com.fforkboat.scanner.token.IdentifierToken;
import j2html.tags.ContainerTag;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.a;
import static j2html.TagCreator.tr;

/**
 * 提供所有的production handler
 * */
public final class ProductionHandlerProvider {
    private ProductionHandlerProvider(){}

    private static Map<Pair<String, String>, ProductionHandler> handlerMap = new HashMap<>();

    public static ProductionHandler getHandler(Pair<String, String> pair){
        return handlerMap.get(pair);
    }

    static {
        handlerMap.put(new Pair<>("T", "TYPE_DEC"), (token) -> {
            switch (token.getTokenType()){
                case INT:
                    ParserContext.getInstance().setDataType(DataType.INT);
                    break;
                case REAL:
                    ParserContext.getInstance().setDataType(DataType.REAL);
                    break;
                case STRING:
                    ParserContext.getInstance().setDataType(DataType.STRING);
                    break;
                case BOOL:
                    ParserContext.getInstance().setDataType(DataType.BOOL);
                    break;
                default:
                    throw new IllegalStateException("Illegal token type:" + token.getTokenType().name() + " here");
            }
        });

        handlerMap.put(new Pair<>("A1", "ID"), token -> {
            ParserContext.getInstance().setLastIdentifierToken((IdentifierToken) token);

        });

        handlerMap.put(new Pair<>("A2", "="), token -> {
            SyntaxTreeContainer currentContainer = ParserContext.getInstance().getCurrentSyntaxTreeContainer();
            IdentifierToken identifierToken = ParserContext.getInstance().getLastIdentifierToken();
            boolean result = currentContainer.addIdentifier(identifierToken.getIdentifierName(), ParserContext.getInstance().getDataType());
            if (!result){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: Identifier '" + identifierToken.getIdentifierName() + "' has been declared", token.getLineIndexOfSourceProgram()));
            }

        });

        handlerMap.put(new Pair<>("A2", "["), token -> {
            switch (ParserContext.getInstance().getDataType()){
                case INT:
                    ParserContext.getInstance().getCurrentSyntaxTreeContainer().addIdentifier(ParserContext.getInstance().getLastIdentifierToken().getIdentifierName(), DataType.INT_ARRAY);
                    break;
                case REAL:
                    ParserContext.getInstance().getCurrentSyntaxTreeContainer().addIdentifier(ParserContext.getInstance().getLastIdentifierToken().getIdentifierName(), DataType.REAL_ARRAY);
                    break;
                case STRING:
                    ParserContext.getInstance().getCurrentSyntaxTreeContainer().addIdentifier(ParserContext.getInstance().getLastIdentifierToken().getIdentifierName(), DataType.STRING_ARRAY);
                    break;
                case BOOL:
                    ParserContext.getInstance().getCurrentSyntaxTreeContainer().addIdentifier(ParserContext.getInstance().getLastIdentifierToken().getIdentifierName(), DataType.BOOL_ARRAY);
                    break;
                default:
                    ParserContext.getInstance().getErrors().add(Error.createCompileError("Illegal structure.", token.getLineIndexOfSourceProgram()));
            }
        });

        handlerMap.put(new Pair<>("T1", "["), token -> {
            switch (ParserContext.getInstance().getDataType()){
                case INT:
                    ParserContext.getInstance().setDataType(DataType.INT_ARRAY);
                    break;
                case REAL:
                    ParserContext.getInstance().setDataType(DataType.REAL_ARRAY);
                    break;
                case STRING:
                    ParserContext.getInstance().setDataType(DataType.STRING_ARRAY);
                    break;
                case BOOL:
                    ParserContext.getInstance().setDataType(DataType.BOOL_ARRAY);
                    break;
                default:
                    throw new IllegalStateException("");
            }
        });

        handlerMap.put(new Pair<>("D2", "{"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("if").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeIfContainer ifContainer = new SyntaxTreeIfContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ifContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(ifContainer);
            ParserContext.getInstance().reset();

            ParserContext.getInstance().setContinueWithNextToken(true);
        });

        // 和G，{的区别在于不把ifContainer设为当前container
        handlerMap.put(new Pair<>("D2", ";"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("if").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeIfContainer ifContainer = new SyntaxTreeIfContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ifContainer);

            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
        });

        // 和G，{的区别在于continueWithThisToken和setInImplicitBlock
        handlerMap.put(new Pair<>("D2", "S"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("if").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeIfContainer ifContainer = new SyntaxTreeIfContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ifContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(ifContainer);
            ParserContext.getInstance().reset();

            ParserContext.getInstance().setContinueWithThisToken(true);
            ParserContext.getInstance().setInImplicitBlock(true);
        });

        handlerMap.put(new Pair<>("G", "{"), token -> {
            List<SyntaxTreeContainerComponent> components = ParserContext.getInstance().getCurrentSyntaxTreeContainer().getComponents();
            SyntaxTreeContainerComponent lastComponent = components.get(components.size()-1);

            SyntaxTreeNormalContainer elseContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), false);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(elseContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("else").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            if (!(lastComponent instanceof SyntaxTreeIfContainer)){
                // TODO 出错处理：else之前没有if
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: no 'if block' before else.", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
            else {
                ((SyntaxTreeIfContainer)lastComponent).setElseConditionContainer(elseContainer);

            }
        });

        // 和G，{的区别在于不把elseContainer设为当前container
        handlerMap.put(new Pair<>("G", ";"), token -> {
            List<SyntaxTreeContainerComponent> components = ParserContext.getInstance().getCurrentSyntaxTreeContainer().getComponents();
            SyntaxTreeContainerComponent lastComponent = components.get(components.size()-1);

            SyntaxTreeNormalContainer elseContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), false);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("else").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            if (!(lastComponent instanceof SyntaxTreeIfContainer)){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: no 'if block' before else.", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
            else {
                ((SyntaxTreeIfContainer)lastComponent).setElseConditionContainer(elseContainer);
            }
        });

        handlerMap.put(new Pair<>("G", "S"), token -> {
            List<SyntaxTreeContainerComponent> components = ParserContext.getInstance().getCurrentSyntaxTreeContainer().getComponents();
            SyntaxTreeContainerComponent lastComponent = components.get(components.size()-1);

            SyntaxTreeNormalContainer elseContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), false);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(elseContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithThisToken(true);
            ParserContext.getInstance().setInImplicitBlock(true);
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("else").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            if (!(lastComponent instanceof SyntaxTreeIfContainer)){
                // TODO 出错处理：else之前没有if
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: no 'if block' before else.", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
            else {
                ((SyntaxTreeIfContainer)lastComponent).setElseConditionContainer(elseContainer);

            }
        });

        handlerMap.put(new Pair<>("E2", "{"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("while").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeWhileContainer whileContainer = new SyntaxTreeWhileContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(whileContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(whileContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
        });

        handlerMap.put(new Pair<>("E2", ";"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("while").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeWhileContainer whileContainer = new SyntaxTreeWhileContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(whileContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
        });

        handlerMap.put(new Pair<>("E2", "S"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("while").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
            SyntaxTreeBranchNode.reverseChildrenSequence(root);
            SyntaxTreeWhileContainer whileContainer = new SyntaxTreeWhileContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), root);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(whileContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(whileContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithThisToken(true);
            ParserContext.getInstance().setInImplicitBlock(true);
        });

        handlerMap.put(new Pair<>("S", "FUNCTION"), token -> {
            if (!ParserContext.getInstance().getCurrentSyntaxTreeContainer().isRootContainer()){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: function declaration in an illegal place", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
        });

        handlerMap.put(new Pair<>("S", "BREAK"), token -> {
            SyntaxTreeContainer container = ParserContext.getInstance().getCurrentSyntaxTreeContainer();
            boolean isInWhile = false;
            while (container != null){
                if (container instanceof SyntaxTreeIfContainer){
                    isInWhile = true;
                    break;
                }
                container = container.getParent();
            }
            if (!isInWhile){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: break clause is not in a while block", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
        });

        handlerMap.put(new Pair<>("S", "CONTINUE"), token -> {
            SyntaxTreeContainer container = ParserContext.getInstance().getCurrentSyntaxTreeContainer();
            boolean isInWhile = false;
            while (container != null){
                if (container instanceof SyntaxTreeIfContainer){
                    isInWhile = true;
                    break;
                }
                container = container.getParent();
            }
            if (!isInWhile){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: continue clause is not in a while block", token.getLineIndexOfSourceProgram()));
                ParserContext.getInstance().setErrorInProductionHandler(true);
            }
        });

        handlerMap.put(new Pair<>("C", "VOID"), token -> {
            ParserContext.getInstance().setVoidReturn(true);
        });

        handlerMap.put(new Pair<>("C1", "ID"), token -> {
            CustomFunction.CustomFunctionBuilder builder = new CustomFunction.CustomFunctionBuilder();
            builder.setFunctionName(((IdentifierToken)token).getIdentifierName());
            builder.setSyntaxTreeContainer(new SyntaxTreeNormalContainer(ParserContext.getInstance().getRootContainer(), false));

            if (ParserContext.getInstance().isVoidReturn()) {
                builder.setFunctionReturnType(ReturnType.VOID);
                ParserContext.getInstance().setVoidReturn(false);
            }
            else
                builder.setFunctionReturnType(ReturnType.valueOf(ParserContext.getInstance().getDataType().name()));

            ParserContext.getInstance().setCurrentFunctionBuilder(builder);
        });

        handlerMap.put(new Pair<>("C311", "ID"), token -> {
            CustomFunction.CustomFunctionBuilder builder = ParserContext.getInstance().getCurrentFunctionBuilder();
            boolean result = builder.addFunctionArgument(ParserContext.getInstance().getDataType(), ((IdentifierToken)token).getIdentifierName());
            if (!result){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: Identifier '" + ((IdentifierToken)token).getIdentifierName() + "' has been declared", token.getLineIndexOfSourceProgram()));
            }
        });

        handlerMap.put(new Pair<>("C5", "{"), token -> {
            CustomFunction function = ParserContext.getInstance().getCurrentFunctionBuilder().build();
            boolean result = FunctionTable.getInstance().addCustomFunction(function);
            if (!result){
                ParserContext.getInstance().getErrors().add(Error.createCompileError("Parser: CustomFunction " + function.getName() + " has already been declared.", token.getLineIndexOfSourceProgram()));
            }

            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("function").withStyle("margin-right:20px").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            ParserContext.getInstance().setInFunction(true);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(function.getSyntaxTreeContainer());
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setContinueWithNextToken(true);
        });

        handlerMap.put(new Pair<>("W", "{"), token -> {
            ParserContext.getInstance().setRightBraceForArray(true);
        });
    }
}
