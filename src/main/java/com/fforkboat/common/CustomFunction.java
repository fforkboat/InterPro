package com.fforkboat.common;

import com.fforkboat.parser.container.SyntaxTreeNormalContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 该类为对函数的抽象
 * 在语法分析过程中遇到合法的函数声明时，会生成一个Function对象，并将其添加到函数表中
 * Function对象是不可变的
 * */
public class CustomFunction extends BaseFunction {
    private SyntaxTreeNormalContainer syntaxTreeContainer;
    private List<String> argumentNames;

    private CustomFunction(String name, ReturnType returnType, List<DataType> argumentTypes, List<String> argumentNames,
                           SyntaxTreeNormalContainer syntaxTreeContainer) {
        super(name, returnType, argumentTypes);
        this.syntaxTreeContainer = syntaxTreeContainer;
        this.argumentNames = Collections.unmodifiableList(argumentNames);
    }

    /**
     * Function对象是不可变的，创建后就无法改变其内部状态（没有提供set函数），但是由于语法分析过程中识别一个函数是分步进行的（先得到返回值、函数名、再得到不确定个数的参数），
     * 所以不可能通过构造函数一步就创建好一个Function对象。为此提供了FunctionBuilder，先创建builder，为builder设置各种属性，最后再由builder创建出Function对象
     * */
    public static class CustomFunctionBuilder {
        private String name;
        private ReturnType returnType;
        private List<DataType> argumentTypes = new ArrayList<>();
        private List<String> argumentNames = new ArrayList<>();
        private SyntaxTreeNormalContainer syntaxTreeContainer;

        public void setFunctionName(String name){
            this.name = name;
        }

        public void setFunctionReturnType(ReturnType returnType) {
            this.returnType = returnType;
        }

        public void setSyntaxTreeContainer(SyntaxTreeNormalContainer syntaxTreeContainer) {
            this.syntaxTreeContainer = syntaxTreeContainer;
        }

        public boolean addFunctionArgument(DataType type, String name) {
            argumentTypes.add(type);
            argumentNames.add(name);
            return syntaxTreeContainer.addIdentifier(name, type);
        }

        public CustomFunction build(){
            return new CustomFunction(name, returnType, argumentTypes, argumentNames, syntaxTreeContainer);
        }
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public SyntaxTreeNormalContainer getSyntaxTreeContainer() {
        return syntaxTreeContainer;
    }
}
