package com.fforkboat.parser;

import com.fforkboat.common.DataType;
import com.fforkboat.common.ReturnType;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;

import java.util.ArrayList;
import java.util.List;


/**
 * 该类为对函数的抽象
 * 在语法分析过程中遇到合法的函数声明时，会生成一个Function对象，并将其添加到函数表中
 * Function对象是不可变的
 * */
public class Function {
    private String name;
    private ReturnType returnType;
    private List<DataType> arguments;
    private SyntaxTreeNormalContainer syntaxTreeContainer;

    private Function(String name, ReturnType returnType, List<DataType> arguments, SyntaxTreeNormalContainer syntaxTreeContainer) {
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
        this.syntaxTreeContainer = syntaxTreeContainer;
    }

    /**
     * Function对象是不可变的，创建后就无法改变其内部状态（没有提供set函数），但是由于语法分析过程中识别一个函数是分步进行的（先得到返回值、函数名、再得到不确定个数的参数），
     * 所以不可能通过构造函数一步就创建好一个Function对象。为此提供了FunctionBuilder，先创建builder，为builder设置各种属性，最后再由builder创建出Function对象
     * */
    static class FunctionBuilder{
        private String name;
        private ReturnType returnType;
        private List<DataType> arguments = new ArrayList<>();
        private SyntaxTreeNormalContainer syntaxTreeContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getRootContainer(), false);

        void setFunctionName(String name){
            this.name = name;
        }

        void setFunctionReturnType(ReturnType returnType) {
            this.returnType = returnType;
        }

        void addFunctionArgument(DataType type, String name) {
            arguments.add(type);
            syntaxTreeContainer.addIdentifier(name, type);
        }

        Function build(){
            return new Function(name, returnType, arguments, syntaxTreeContainer);
        }
    }

    public String getName() {
        return name;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public List<DataType> getArguments() {
        return arguments;
    }

    public SyntaxTreeNormalContainer getSyntaxTreeContainer() {
        return syntaxTreeContainer;
    }
}
