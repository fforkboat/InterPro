package com.fforkboat.interpreter;

import com.fforkboat.common.Error;
import com.fforkboat.parser.container.SyntaxTreeContainer;

class InterpreterContext {
    private static boolean continueToExecute = true;
    private static boolean encounterBreak = false;
    private static boolean encounterContinue = false;
    private static SyntaxTreeContainer currentContainer;

    static boolean isContinueToExecute() {
        return continueToExecute;
    }

    static void setContinueToExecute(boolean continueToExecute) {
        InterpreterContext.continueToExecute = continueToExecute;
    }

    static SyntaxTreeContainer getCurrentContainer() {
        return currentContainer;
    }

    static void setCurrentContainer(SyntaxTreeContainer currentContainer) {
        InterpreterContext.currentContainer = currentContainer;
    }

    static boolean isEncounterBreak() {
        return encounterBreak;
    }

    static void setEncounterBreak(boolean encounterBreak) {
        InterpreterContext.encounterBreak = encounterBreak;
    }

    static boolean isEncounterContinue() {
        return encounterContinue;
    }

    static void setEncounterContinue(boolean encounterContinue) {
        InterpreterContext.encounterContinue = encounterContinue;
    }

    static void throwError(Error error){
        System.out.println(error);
        continueToExecute = false;
    }
}
