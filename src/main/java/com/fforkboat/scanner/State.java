package com.fforkboat.scanner;

import org.javatuples.Pair;

import java.util.*;


/**
 * 一个State对象代表自动机中的一个状态。
 * State对象是不可变的，创建之后无法改变其数据成员。
 *
* */
class State {
    private String name;
    private boolean isFinalState;
    // state所对应的token类型，当该状态是中间状态时，correspondingTokeType为null
    private TokenType correspondingTokeType;
    // state的状态转移信息，接收一个GeneralChar后，进入下一个state
    private List<Pair<GeneralChar, State>> stateTransferList;

    // State的工厂类，用于创建各种state
    static class StateBuilder{
        private StateBuilder(){}
        private static StateBuilder stateBuilder = new StateBuilder();
        static StateBuilder getInstance(){
            return stateBuilder;
        }


        /**
         * 创建一个intermediate state，该state不存在环(接收一个GeneralChar后的目标state是自己)
         * */
        State createIntermediateState(List<Pair<GeneralChar, State>> stateTransfer, String name){
            return new State(stateTransfer, false, null, name);
        }

        /**
         * 创建一个intermediate state，该state存在环
         * */
        State createIntermediateStateWithLoop(List<Pair<GeneralChar, State>> stateTransfer, GeneralChar[] charsLeadToLoop, String name){
            State state = new State();
            for (GeneralChar c : charsLeadToLoop) {
                stateTransfer.add(new Pair<GeneralChar, State>(c, state));
            }

            initState(state, stateTransfer, false, null, name);
            return state;
        }

        /**
         * 创建一个final state，该state不存在环(接收一个GeneralChar后的目标state是自己)
         */
        State createFinalState(List<Pair<GeneralChar, State>> stateTransfer, TokenType type, String name){
            return new State(stateTransfer, true, type, name);
        }

        /**
         * 创建一个final state，该state存在环
         * */
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

    /**
     * 判断state是否能接受字符c
     * */
    boolean canReceive(char c){
        for (Pair<GeneralChar, State> pair :
                stateTransferList) {
            if (pair.getValue0().canReceive(c))
                return true;
        }

        return false;
    }

    /**
     * state在接受字符c后进入的状态
     * 如果不能接受c，返回null
     * */
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
