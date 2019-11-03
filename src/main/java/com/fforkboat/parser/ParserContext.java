package com.fforkboat.parser;

import com.fforkboat.common.DataType;
import com.fforkboat.parser.container.SyntaxTreeContainer;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;
import com.fforkboat.parser.symbol.NonterminalSymbol;
import com.fforkboat.parser.symbol.Symbol;
import com.fforkboat.parser.symbol.TerminalSymbol;
import com.fforkboat.parser.tree.*;
import com.fforkboat.scanner.token.TokenType;
import j2html.tags.ContainerTag;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.javatuples.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.*;

import static j2html.TagCreator.*;

class ParserContext {
    private ParserContext(){
        setNotFullyConfiguredBean();
    }
    private static ParserContext context = new ParserContext();
    static ParserContext getInstance(){
        return context;
    }

    private DataType dataType;
    private SyntaxTreeNormalContainer rootContainer;
    private SyntaxTreeContainer currentSyntaxTreeContainer;
    private SyntaxTreeBranchNode syntaxTreeRoot;
    private Function.FunctionBuilder currentFunctionBuilder;
    private boolean rightBraceForArray = false;
    private boolean shouldContinue = false;
    private boolean voidReturn = false;
    private Stack<Symbol> symbolStack = new Stack<>();
    private Stack<SyntaxTreeNode> nodeStack = new Stack<>();
    private Map<SyntaxTreeContainer, Pair<ContainerTag, Integer>> HTMLContainers = new HashMap<>();

    private ApplicationContext springContext = new FileSystemXmlApplicationContext("src/main/resources/META-INF/parserContext.xml");;


    DataType getDataType() {
        return dataType;
    }

    void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    SyntaxTreeNormalContainer getRootContainer() {
        return rootContainer;
    }

    void setRootContainer(SyntaxTreeNormalContainer rootContainer) {
        this.rootContainer = rootContainer;
    }

    SyntaxTreeContainer getCurrentSyntaxTreeContainer() {
        return currentSyntaxTreeContainer;
    }

    void setCurrentSyntaxTreeContainer(SyntaxTreeContainer currentSyntaxTreeContainer) {
        if (!HTMLContainers.containsKey(currentSyntaxTreeContainer)){
            HTMLContainers.put(currentSyntaxTreeContainer, new Pair<>(html(), HTMLContainers.size()));
        }
        this.currentSyntaxTreeContainer = currentSyntaxTreeContainer;
    }

    SyntaxTreeBranchNode getSyntaxTreeRoot() {
        return syntaxTreeRoot;
    }

    void setSyntaxTreeRoot(SyntaxTreeBranchNode syntaxTreeRoot) {
        this.syntaxTreeRoot = syntaxTreeRoot;
    }

    boolean isRightBraceForArray() {
        return rightBraceForArray;
    }

    void setRightBraceForArray(boolean rightBraceForArray) {
        this.rightBraceForArray = rightBraceForArray;
    }

    Stack<Symbol> getSymbolStack() {
        return symbolStack;
    }

    Stack<SyntaxTreeNode> getNodeStack() {
        return nodeStack;
    }

    boolean isShouldContinue() {
        return shouldContinue;
    }

    void setShouldContinue(boolean shouldContinue) {
        this.shouldContinue = shouldContinue;
    }

    Function.FunctionBuilder getCurrentFunctionBuilder() {
        return currentFunctionBuilder;
    }

    void setCurrentFunctionBuilder(Function.FunctionBuilder currentFunctionBuilder) {
        this.currentFunctionBuilder = currentFunctionBuilder;
    }

    boolean isVoidReturn() {
        return voidReturn;
    }

    void setVoidReturn(boolean voidReturn) {
        this.voidReturn = voidReturn;
    }

    ContainerTag getHtmlContainer(SyntaxTreeContainer syntaxTreeContainer){
        return HTMLContainers.get(syntaxTreeContainer).getValue0();
    }

    Collection<Pair<ContainerTag, Integer>> getAllHtmlContainers(){
        return HTMLContainers.values();
    }

    int getNextHtmlContainerIndex(){
        return HTMLContainers.size();
    }

    void reset(){
        symbolStack.clear();
        nodeStack.clear();

        syntaxTreeRoot = new SyntaxTreeBranchNode("S", null);
        symbolStack.push((NonterminalSymbol) springContext.getBean("S"));
        nodeStack.push(syntaxTreeRoot);
    }

    void addSyntaxTreeToContainer(){
        SyntaxTreeBranchNode root = ParserContext.getInstance().getSyntaxTreeRoot();
        SyntaxTreeBranchNode.reverseChildrenSequence(root);

        SyntaxTreeAsTreeForTreeLayout layout = new SyntaxTreeAsTreeForTreeLayout(root);
        DefaultConfiguration<SyntaxTreeNode> configuration = new DefaultConfiguration<>(50, 10);
        SyntaxTreeNodeExtentProvider provider = new SyntaxTreeNodeExtentProvider();
        TreeLayout<SyntaxTreeNode> treeLayout = new TreeLayout<>(layout, provider, configuration);
        SVGForSyntaxTree generator = new SVGForSyntaxTree(treeLayout);

        ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
        container.with(rawHtml(generator.getSVG()));

        ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ParserContext.getInstance().getSyntaxTreeRoot());
        ParserContext.getInstance().reset();
    }

    @SuppressWarnings("unchecked")
    private void setNotFullyConfiguredBean(){
        NonterminalSymbol C32 = (NonterminalSymbol) springContext.getBean("C32");
        NonterminalSymbol C31 = (NonterminalSymbol) springContext.getBean("C31");
        TerminalSymbol COMMA = (TerminalSymbol) springContext.getBean("COMMA");

        C32.setProductions(new HashMap(){
            {
                put(TokenType.COMMA, new Pair(new ArrayList(){
                    {
                        add(COMMA);
                        add(C31);
                    }
                }, null));
                put(TokenType.RIGHT_PARENTHESIS, new Pair(new ArrayList(){
                    {

                    }
                }, null));
            }
        });

        NonterminalSymbol X2 = (NonterminalSymbol) springContext.getBean("X2");
        NonterminalSymbol X = (NonterminalSymbol) springContext.getBean("X");
        TerminalSymbol ADD = (TerminalSymbol) springContext.getBean("+");
        TerminalSymbol SUBTRACT = (TerminalSymbol) springContext.getBean("-");
        X2.addProduction(TokenType.ADD, new Pair(new ArrayList(){
            {
                add(ADD);
                add(X);
            }
        }, null));
        X2.addProduction(TokenType.SUBTRACT, new Pair(new ArrayList(){
            {
                add(SUBTRACT);
                add(X);
            }
        }, null));

        NonterminalSymbol X3 = (NonterminalSymbol) springContext.getBean("X3");
        TerminalSymbol LEFT_PARENTHESIS = (TerminalSymbol) springContext.getBean("(");
        TerminalSymbol RIGHT_PARENTHESIS = (TerminalSymbol) springContext.getBean(")");
        X3.addProduction(TokenType.LEFT_PARENTHESIS, new Pair(new ArrayList(){
            {
                add(LEFT_PARENTHESIS);
                add(X);
                add(RIGHT_PARENTHESIS);
            }
        }, null));

        NonterminalSymbol X4 = (NonterminalSymbol) springContext.getBean("X4");
        NonterminalSymbol X1 = (NonterminalSymbol) springContext.getBean("X1");
        TerminalSymbol MULTIPLY = (TerminalSymbol) springContext.getBean("*");
        TerminalSymbol DIVIDE = (TerminalSymbol) springContext.getBean("/");
        X4.addProduction(TokenType.MULTIPLY, new Pair(new ArrayList(){
            {
                add(MULTIPLY);
                add(X1);
            }
        }, null));
        X4.addProduction(TokenType.DIVIDE, new Pair(new ArrayList(){
            {
                add(DIVIDE);
                add(X1);
            }
        }, null));

        NonterminalSymbol Z = (NonterminalSymbol) springContext.getBean("Z");
        TerminalSymbol R_OPERATOR = (TerminalSymbol) springContext.getBean("R_OPERATOR");
        Z.addProduction(TokenType.ClusterFeature.RELATIONAL_OPERATOR, new Pair(new ArrayList(){
            {
                add(R_OPERATOR);
                add(X);
            }
        }, null));

        NonterminalSymbol U1 = (NonterminalSymbol) springContext.getBean("U1");
        TerminalSymbol LEFT_BRACKET = (TerminalSymbol) springContext.getBean("[");
        TerminalSymbol RIGHT_BRACKET = (TerminalSymbol) springContext.getBean("]");
        U1.addProduction(TokenType.LEFT_BRACKET, new Pair(new ArrayList(){
            {
                add(LEFT_BRACKET);
                add(X);
                add(RIGHT_BRACKET);
            }
        }, null));

        NonterminalSymbol U11 = (NonterminalSymbol) springContext.getBean("U11");
        NonterminalSymbol U12 = (NonterminalSymbol) springContext.getBean("U12");
        U12.addProduction(TokenType.COMMA, new Pair(new ArrayList(){
            {
                add(COMMA);
                add(U11);
            }
        }, null));

        NonterminalSymbol W1 = (NonterminalSymbol) springContext.getBean("W1");
        NonterminalSymbol W21 = (NonterminalSymbol) springContext.getBean("W21");
        NonterminalSymbol N = (NonterminalSymbol) springContext.getBean("N");
        TerminalSymbol STRING_LITERAL = (TerminalSymbol) springContext.getBean("STRING_LITERAL");
        TerminalSymbol BOOL_LITERAL = (TerminalSymbol) springContext.getBean("BOOL_LITERAL");
        W1.setProductions(new HashMap(){
            {
                put(TokenType.IDENTIFIER, new Pair(new ArrayList(){
                    {
                        add(N);
                    }
                }, null));
                put(TokenType.LEFT_PARENTHESIS, new Pair(new ArrayList(){
                    {
                        add(N);
                    }
                }, null));
                put(TokenType.ClusterFeature.NUMBER, new Pair(new ArrayList(){
                    {
                        add(N);
                    }
                }, null));
                put(TokenType.STRING_LITERAL, new Pair(new ArrayList(){
                    {
                        add(STRING_LITERAL);
                    }
                }, null));
                put(TokenType.BOOL_LITERAL, new Pair(new ArrayList(){
                    {
                        add(BOOL_LITERAL);
                    }
                }, null));
                put(TokenType.ADD, new Pair(new ArrayList(){
                    {
                        add(N);
                    }
                }, null));
                put(TokenType.SUBTRACT, new Pair(new ArrayList(){
                    {
                        add(N);
                    }
                }, null));
            }
        });
        W21.addProduction(TokenType.COMMA, new Pair(new ArrayList(){
            {
                add(COMMA);
                add(W1);
                add(W21);
            }
        }, null));
    }


}
