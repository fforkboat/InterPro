package com.fforkboat.common;

import java.util.List;

public class Error {
    private String error;

    private Error(String error) {
        this.error = error;
    }

    public static Error createCompileError(String cause, int lineCount){
        return new Error("Compile error at line " + lineCount + ": \n\t" + cause);
    }

    public static Error createRuntimeError(String cause, int lineCount){
        return new Error("Runtime error at line " + lineCount + ": \n\t" + cause);
    }

    @Override
    public String toString() {
        return error;
    }

    public static void printErrorList(List<Error> errors){
        for (Error error :
                errors) {
            System.out.println(error.toString());
        }
    }
}
