package com.fforkboat.common;

import java.util.List;

public class CompileError {
    private String error;

    public CompileError(String cause, int lineCount){
        error = "Compile error at line " + lineCount + ": " + cause;
    }

    @Override
    public String toString() {
        return error;
    }

    public static void printErrorList(List<CompileError> errors){
        for (CompileError error :
                errors) {
            System.out.println(error.toString());
        }
    }
}
