package com.fforkboat.program;

import com.fforkboat.common.*;

import java.util.*;

public class FunctionTable {
    private Map<String, BaseFunction> functionMap = new HashMap<>();

    private static FunctionTable instance = new FunctionTable();
    private FunctionTable(){
        functionMap.put("println", new BuiltInFunction("println", ReturnType.VOID, Collections.singletonList(DataType.OBJECT),
                list ->{
                    if (list.get(0) instanceof Object[])
                        System.out.println(Arrays.toString((Object[]) list.get(0)));
                    else
                        System.out.println(list.get(0));
                    return null;
                }));
        functionMap.put("print", new BuiltInFunction("print", ReturnType.VOID, Collections.singletonList(DataType.OBJECT),
                list ->{
                    if (list.get(0) instanceof Object[])
                        System.out.print(Arrays.toString((Object[]) list.get(0)));
                    else
                        System.out.print(list.get(0));
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

        return functionMap.get(functionName);
    }

}
