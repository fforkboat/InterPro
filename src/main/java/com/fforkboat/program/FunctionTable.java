package com.fforkboat.program;

import com.fforkboat.parser.Function;

import java.util.HashMap;
import java.util.Map;

public class FunctionTable {
    private Map<String, Function> functionMap = new HashMap<>();

    private static FunctionTable instance = new FunctionTable();
    private FunctionTable(){}
    public static FunctionTable getInstance(){
        return instance;
    }

    public void addFunction(Function function){
        functionMap.put(function.getName(), function);
    }

    public Function getFunction(String functionName){
        if (!functionMap.containsKey(functionName)){
            // TODO 出错处理
            return null;
        }

        return functionMap.get(functionName);
    }

}
