package com.fforkboat.parser;

import com.fforkboat.common.CustomFunction;
import com.fforkboat.common.Error;
import com.fforkboat.common.DataType;
import com.fforkboat.parser.container.SyntaxTreeContainer;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;
import com.fforkboat.parser.symbol.NonterminalSymbol;
import com.fforkboat.parser.symbol.Symbol;
import com.fforkboat.parser.symbol.TerminalSymbol;
import com.fforkboat.parser.tree.*;
import com.fforkboat.scanner.token.IdentifierToken;
import com.fforkboat.scanner.token.TokenType;
import j2html.tags.ContainerTag;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.javatuples.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.*;

import static j2html.TagCreator.*;

/**
 * 语法分析器在进行语法分析过程中的上下文，保存了各种有用的数据
 * 该类为单例类
 * */
public class ParserContext {
    private ParserContext(){
        setNotFullyConfiguredBean();
    }
    private static ParserContext context = new ParserContext();
    static ParserContext getInstance(){
        return context;
    }

    // 保存一个标识符、函数参数、函数返回值的数据类型
    private DataType dataType;

    private SyntaxTreeNormalContainer rootContainer;
    private SyntaxTreeContainer currentSyntaxTreeContainer;
    private SyntaxTreeBranchNode currentSyntaxTreeRoot;
    private CustomFunction.CustomFunctionBuilder currentFunctionBuilder;

    private IdentifierToken lastIdentifierToken;

    // 记录最后读取到的{符号是否是数组声明（{1, 2, 3}）的一部分，用于和块定义(if, while, function等语法块)的{符号区别开
    private boolean rightBraceForArray = false;

    private boolean continueWithNextToken = false;
    private boolean continueWithThisToken = false;
    private boolean inImplicitBlock = false;
    private boolean errorInProductionHandler = false;
    private boolean voidReturn = false;
    private boolean inFunction = false;

    // 符号栈
    private Stack<Symbol> symbolStack = new Stack<>();

    // 树节点栈
    private Stack<SyntaxTreeNode> nodeStack = new Stack<>();

    // 语法树容器与其相应的HTML的映射
    private Map<SyntaxTreeContainer, Pair<ContainerTag, Integer>> HTMLContainers = new HashMap<>();

    // 语法分析过程中发现的错误
    private List<Error> errors = new ArrayList<>();

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

    SyntaxTreeBranchNode getCurrentSyntaxTreeRoot() {
        return currentSyntaxTreeRoot;
    }

    void setCurrentSyntaxTreeRoot(SyntaxTreeBranchNode currentSyntaxTreeRoot) {
        this.currentSyntaxTreeRoot = currentSyntaxTreeRoot;
    }

    IdentifierToken getLastIdentifierToken() {
        return lastIdentifierToken;
    }

    void setLastIdentifierToken(IdentifierToken lastIdentifierToken) {
        this.lastIdentifierToken = lastIdentifierToken;
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

    boolean isContinueWithNextToken() {
        return continueWithNextToken;
    }

    void setContinueWithNextToken(boolean continueWithNextToken) {
        this.continueWithNextToken = continueWithNextToken;
    }

    boolean isContinueWithThisToken() {
        return continueWithThisToken;
    }

    void setContinueWithThisToken(boolean continueWithThisToken) {
        this.continueWithThisToken = continueWithThisToken;
    }

    boolean isInImplicitBlock() {
        return inImplicitBlock;
    }

    void setInImplicitBlock(boolean inImplicitBlock) {
        this.inImplicitBlock = inImplicitBlock;
    }

    boolean isErrorInProductionHandler() {
        return errorInProductionHandler;
    }

    void setErrorInProductionHandler(boolean errorInProductionHandler) {
        this.errorInProductionHandler = errorInProductionHandler;
    }

    public boolean isInFunction() {
        return inFunction;
    }

    public void setInFunction(boolean inFunction) {
        this.inFunction = inFunction;
    }

    CustomFunction.CustomFunctionBuilder getCurrentFunctionBuilder() {
        return currentFunctionBuilder;
    }

    void setCurrentFunctionBuilder(CustomFunction.CustomFunctionBuilder currentFunctionBuilder) {
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

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    void reset(){
        symbolStack.clear();
        nodeStack.clear();

        currentSyntaxTreeRoot = new SyntaxTreeBranchNode("S", null);
        symbolStack.push((NonterminalSymbol) springContext.getBean("S"));
        nodeStack.push(currentSyntaxTreeRoot);
    }

    // 当读取到;时，将当前的语法树加入到当前的语法树容器中
    void addSyntaxTreeToContainer(){
        SyntaxTreeBranchNode root = ParserContext.getInstance().getCurrentSyntaxTreeRoot();
        SyntaxTreeBranchNode.reverseChildrenSequence(root);

        SyntaxTreeAsTreeForTreeLayout layout = new SyntaxTreeAsTreeForTreeLayout(root);
        DefaultConfiguration<SyntaxTreeNode> configuration = new DefaultConfiguration<>(50, 10);
        SyntaxTreeNodeExtentProvider provider = new SyntaxTreeNodeExtentProvider();
        TreeLayout<SyntaxTreeNode> treeLayout = new TreeLayout<>(layout, provider, configuration);
        SVGForSyntaxTree generator = new SVGForSyntaxTree(treeLayout);

        ContainerTag container = ParserContext.getInstance().getHtmlContainer(ParserContext.getInstance().getCurrentSyntaxTreeContainer());
        container.with(rawHtml(generator.getSVG()));

        ParserContext.getInstance().getCurrentSyntaxTreeContainer().addComponent(ParserContext.getInstance().getCurrentSyntaxTreeRoot());
        ParserContext.getInstance().reset();
        if (inImplicitBlock){
            currentSyntaxTreeContainer = currentSyntaxTreeContainer.getParent();
            inImplicitBlock = false;
        }
    }

    // 因为循环引用的原因，在Spring中没法完全初始化一些bean，于是拿到这里来进行后续工作
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

        NonterminalSymbol M2 = (NonterminalSymbol) springContext.getBean("M2");
        M2.addProduction(TokenType.COMMA, new Pair(new ArrayList(){
            {
                add(COMMA);
                add(W1);
                add(M2);
            }
        }, null));
    }
}
