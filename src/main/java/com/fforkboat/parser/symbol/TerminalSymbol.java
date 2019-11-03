package com.fforkboat.parser.symbol;


/**
 * 终结符
 * */
public class TerminalSymbol implements Symbol {
    private TerminalSymbolIndicator indicator;

    public TerminalSymbol(TerminalSymbolIndicator indicator) {
        this.indicator = indicator;
    }

    public TerminalSymbolIndicator getIndicator() {
        return indicator;
    }

    @Override
    public String toString() {
        return indicator.toString();
    }
}
