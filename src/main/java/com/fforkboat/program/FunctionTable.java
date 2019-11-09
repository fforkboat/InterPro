package com.fforkboat.program;

import com.fforkboat.common.*;

import java.util.*;

public class FunctionTable {
    private Map<String, BaseFunction> functionMap = new HashMap<>();

    private static FunctionTable instance = new FunctionTable();
    private FunctionTable(){
        functionMap.put("print", new BuiltInFunction("print", ReturnType.VOID, Collections.singletonList(DataType.OBJECT),
                list ->{
                    System.out.println(list.get(0));
                    return null;
                }));
        functionMap.put("scan", new BuiltInFunction("scan", ReturnType.STRING, new ArrayList<>(),
                list ->{
                    Scanner scanner = new Scanner(System.in);
                    return scanner.nextLine();
                }));
    }
    public static FunctionTable getInstance(){
        return instance;
    }

    public boolean addCustomFunction(CustomFunction function){
        if (functionMap.containsKey(function.getName()))
            return false;

        functionMap.put(function.getName(), function);
        return true;
    }

    public BaseFunction getFunction(String functionName){
        if (!functionMap.containsKey(functionName)){
            return null;
        }

        return functionMap.get(functionName);
    }

}
