package com.fforkboat.scanner;

import org.javatuples.Pair;

import java.util.*;


/**
 * 一个State对象代表自动机中的一个状态。
* */
class State {
    private String name;
    private boolean isFinalState;
    private TokenType correspondingTokeType;
    private List<Pair<GeneralChar, State>> stateTransferList;

    static class StateBuilder{
        private StateBuilder(){}
        private static StateBuilder stateBuilder = new StateBuilder();
        static StateBuilder getInstance(){
            return stateBuilder;
        }

        State createIntermediateState(List<Pair<GeneralChar, State>> stateTransfer, String name){
            return new State(stateTransfer, false, null, name);
        }

        State createIntermediateStateWithLoop(List<Pair<GeneralChar, State>> stateTransfer, GeneralChar[] charsLeadToLoop, String name){
            State state = new State();
            for (GeneralChar c : charsLeadToLoop) {
                stateTransfer.add(new Pair<GeneralChar, State>(c, state));
            }

            initState(state, stateTransfer, false, null, name);
            return state;
        }

        State createFinalState(List<Pair<GeneralChar, State>> stateTransfer, TokenType type, String name){
            return new State(stateTransfer, true, type, name);
        }

        State createFinalStateWithLoop(List<Pair<GeneralChar, State>> stateTransfer, TokenType type, GeneralChar[] charsLeadToLoop, String name){
            State state = new State();
            for (GeneralChar c : charsLeadToLoop) {
                stateTransfer.add(new Pair<GeneralChar, State>(c, state));
            }

            initState(state, stateTransfer, true, type, name);
            return state;
        }

    }

    private State(){}
    private State(List<Pair<GeneralChar, State>> stateTransfer, boolean isFinal, TokenType type, String name){
        initState(this, stateTransfer, isFinal, type, name);
    }
    private static void initState(State state, List<Pair<GeneralChar, State>> stateTransfer, boolean isFinal, TokenType type, String name){
        state.stateTransferList = new ArrayList<>();
        state.stateTransferList.addAll(stateTransfer);
        state.isFinalState = isFinal;
        state.correspondingTokeType = type;
        state.name = name;
    }


    public String getName() {
        return name;
    }

    TokenType getCorrespondingTokeType() {
        return correspondingTokeType;
    }

    boolean isFinalState() {
        return isFinalState;
    }

    boolean canReceive(char c){
        for (Pair<GeneralChar, State> pair :
                stateTransferList) {
            if (pair.getValue0().canReceive(c))
                return true;
        }

        return false;
    }

    State getNextState(char c){
        int count = 0;
        State state = null;
        for (Pair<GeneralChar, State> pair :
                stateTransferList) {
            if (pair.getValue0().canReceive(c)){
                count++;
                if (count != 1){
                    throw new IllegalStateException("Find more than one next-state at state:" + name +
                            "This should not occur in a DFA." +
                            "Check the state-transfer you have set before, and make sure for a specific character, " +
                            "the current state will transfer to only one next-state");
                }
                else {
                    state = pair.getValue1();
                }
            }
        }

        return state;
    }
}
