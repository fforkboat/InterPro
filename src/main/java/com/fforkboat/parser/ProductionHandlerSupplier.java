package com.fforkboat.parser;

import com.fforkboat.common.DataType;
import com.fforkboat.common.ReturnType;
import com.fforkboat.parser.container.SyntaxTreeContainerComponent;
import com.fforkboat.parser.container.SyntaxTreeIfContainer;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;
import com.fforkboat.parser.container.SyntaxTreeWhileContainer;
import com.fforkboat.program.FunctionTable;
import com.fforkboat.scanner.token.IdentifierToken;
import j2html.tags.ContainerTag;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.a;

public class ProductionHandlerSupplier {
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
                case DOUBLE:
                    ParserContext.getInstance().setDataType(DataType.DOUBLE);
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
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addIdentifier(((IdentifierToken)token).getIdentifierName(), ParserContext.getInstance().getDataType());
        });

        handlerMap.put(new Pair<>("T1", "["), token -> {
            switch (ParserContext.getInstance().getDataType()){
                case INT:
                    ParserContext.getInstance().setDataType(DataType.INT_ARRAY);
                    break;
                case DOUBLE:
                    ParserContext.getInstance().setDataType(DataType.DOUBLE_ARRAY);
                    break;
                case STRING:
                    ParserContext.getInstance().setDataType(DataType.STRING_ARRAY);
                    break;
                case BOOL:
                    ParserContext.getInstance().setDataType(DataType.BOOL_ARRAY);
                    break;
                default:
                    // TODO: explain exception reason
                    throw new IllegalStateException("");
            }
        });

        handlerMap.put(new Pair<>("D2", "{"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("if").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeIfContainer ifContainer = new SyntaxTreeIfContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), ParserContext.getInstance().getSyntaxTreeRoot());
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ifContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(ifContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setShouldContinue(true);
        });

        handlerMap.put(new Pair<>("G", "{"), token -> {
            List<SyntaxTreeContainerComponent> components = ParserContext.getInstance().getCurrentSyntaxTreeContainer().getComponents();
            SyntaxTreeContainerComponent lastComponent = components.get(components.size()-1);
            if (!(lastComponent instanceof SyntaxTreeIfContainer)){
                // TODO 出错处理：else之前没有if
                return;
            }

            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("else").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeNormalContainer elseContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), false);
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(elseContainer);
            ((SyntaxTreeIfContainer)lastComponent).setElseConditionContainer(elseContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(elseContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setShouldContinue(true);
        });

        handlerMap.put(new Pair<>("E2", "{"), token -> {
            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("while").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            SyntaxTreeWhileContainer whileContainer = new SyntaxTreeWhileContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer(), ParserContext.getInstance().getSyntaxTreeRoot());
            ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(whileContainer);
            ParserContext.getInstance().setCurrentSyntaxTreeContainer(whileContainer);
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setShouldContinue(true);
        });

        handlerMap.put(new Pair<>("S", "FUNCTION"), token -> {
            if (!ParserContext.getInstance().getCurrentSyntaxTreeContainer().isRootContainer()){
                // TODO 报错处理
            }
        });

        handlerMap.put(new Pair<>("C", "VOID"), token -> {
            ParserContext.getInstance().setVoidReturn(true);
        });

        handlerMap.put(new Pair<>("C1", "ID"), token -> {
            Function.FunctionBuilder builder = new Function.FunctionBuilder();
            builder.setFunctionName(((IdentifierToken)token).getIdentifierName());

            if (ParserContext.getInstance().isVoidReturn()) {
                builder.setFunctionReturnType(ReturnType.VOID);
                ParserContext.getInstance().setVoidReturn(false);
            }
            else
                builder.setFunctionReturnType(ReturnType.valueOf(ParserContext.getInstance().getDataType().name()));

            ParserContext.getInstance().setCurrentFunctionBuilder(builder);
        });

        handlerMap.put(new Pair<>("C311", "ID"), token -> {
            Function.FunctionBuilder builder = ParserContext.getInstance().getCurrentFunctionBuilder();
            builder.addFunctionArgument(ParserContext.getInstance().getDataType(), ((IdentifierToken)token).getIdentifierName());
        });

        handlerMap.put(new Pair<>("C5", "{"), token -> {
            Function function = ParserContext.getInstance().getCurrentFunctionBuilder().build();
            FunctionTable.getInstance().addFunction(function);

            ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
            container.with(a("function").withHref(ParserContext.getInstance().getNextHtmlContainerIndex()+".html"));

            ParserContext.getInstance().setCurrentSyntaxTreeContainer(function.getSyntaxTreeContainer());
            ParserContext.getInstance().reset();
            ParserContext.getInstance().setShouldContinue(true);
        });

        handlerMap.put(new Pair<>("W", "{"), token -> {
            ParserContext.getInstance().setRightBraceForArray(true);
        });
    }
}
