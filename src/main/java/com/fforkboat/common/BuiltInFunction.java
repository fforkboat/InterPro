package com.fforkboat.common;

import java.util.List;

public class BuiltInFunction extends BaseFunction {
    private Handler handler;

    public BuiltInFunction(String name, ReturnType returnType, List<DataType> argumentTypes, Handler handler) {
        super(name, returnType, argumentTypes);
        this.handler = handler;
    }

    public Object call(List<Object> arguments) {
        return handler.handle(arguments);
    }

    public interface Handler{
        Object handle(List<Object> arguments);
    }
}
