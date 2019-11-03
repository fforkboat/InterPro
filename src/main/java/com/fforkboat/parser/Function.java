package com.fforkboat.parser;

import com.fforkboat.common.DataType;
import com.fforkboat.common.ReturnType;
import com.fforkboat.parser.container.SyntaxTreeNormalContainer;

import java.util.ArrayList;
import java.util.List;

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

    public static class FunctionBuilder{
        private String name;
        private ReturnType returnType;
        private List<DataType> arguments = new ArrayList<>();
        private SyntaxTreeNormalContainer syntaxTreeContainer = new SyntaxTreeNormalContainer(ParserContext.getInstance().getRootContainer(), false);

        public void setFunctionName(String name){
            this.name = name;
        }

        public void setFunctionReturnType(ReturnType returnType) {
            this.returnType = returnType;
        }

        public void addFunctionArgument(DataType type, String name) {
            arguments.add(type);
            syntaxTreeContainer.addIdentifier(name, type);
        }

        public void setFunctionSyntaxTrees(SyntaxTreeNormalContainer syntaxTrees) {
            this.syntaxTreeContainer = syntaxTrees;
        }

        public Function build(){
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
