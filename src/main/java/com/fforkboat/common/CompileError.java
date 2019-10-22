package com.fforkboat.common;

public class CompileError {
    private String error;

    public CompileError(String cause, int lineCount){
        error = "Compile error at line " + lineCount + ": " + cause;
    }

    @Override
    public String toString() {
        return error;
    }
}
