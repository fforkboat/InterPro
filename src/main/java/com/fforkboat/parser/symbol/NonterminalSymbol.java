package com.fforkboat.parser.symbol;

import com.fforkboat.parser.ProductionHandler;
import com.fforkboat.scanner.token.Token;

import com.fforkboat.scanner.token.TokenType;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 非终结符
 * 本来打算将此类也设计成像词法分析器中的State一样的不可变类。但是因为产生式的间接递归太难处理了，最后还是把此类设计为了可变类，
 * 先创建未完全配置好的对象，再进行补充配置（可参见ParserContext中的setNotFullyConfiguredBean方法)
 * */
public class NonterminalSymbol implements Symbol {
    private Map<TerminalSymbolIndicator, Pair<List<Symbol>, ProductionHandler>> productions;
    private String symbolName;

    private NonterminalSymbol(String symbolName, Map<TerminalSymbolIndicator, Pair<List<Symbol>, ProductionHandler>> productions) {
        this.productions = productions;
        this.symbolName = symbolName;
    }

    // 设计失误
    static class NonterminalSymbolBuilder{
        private static NonterminalSymbolBuilder builder = new NonterminalSymbolBuilder();
        private NonterminalSymbolBuilder(){}
        static NonterminalSymbolBuilder getBuilder(){return builder;}

        // 设计失误，忘记考虑间接递归的情况了
        NonterminalSymbol createNonterminalSymbolWithRecursion(String symbolName, Map<TerminalSymbolIndicator, Pair<List<Symbol>, ProductionHandler>> map, Map<TerminalSymbolIndicator, Triplet<List<Symbol>, ProductionHandler, Integer>> recursionMap){
            NonterminalSymbol symbol = new NonterminalSymbol(null, null);

            for (TerminalSymbolIndicator indicator :
                    recursionMap.keySet()) {
                Triplet<List<Symbol>, ProductionHandler, Integer> triplet = recursionMap.get(indicator);
                triplet.getValue0().add(triplet.getValue2(), symbol);
                map.put(indicator, new Pair<>(triplet.getValue0(), triplet.getValue1()));
            }

            symbol.productions = map;
            symbol.symbolName = symbolName;

            return symbol;
        }

        NonterminalSymbol createNonterminalSymbolWithoutRecursion(String symbolName, Map<TerminalSymbolIndicator, Pair<List<Symbol>, ProductionHandler>> map){
            return new NonterminalSymbol(symbolName, map);
        }
    }

    public void setProductions(Map<TerminalSymbolIndicator, Pair<List<Symbol>, ProductionHandler>> productions) {
        Objects.requireNonNull(productions);

        this.productions = productions;
    }

    public void addProduction(TerminalSymbolIndicator indicator, Pair<List<Symbol>, ProductionHandler> pair){
        this.productions.put(indicator, pair);
    }

    public String getSymbolName() {
        return symbolName;
    }

    public List<Symbol> getRightPart(Token token){
        Pair<List<Symbol>, ProductionHandler> pair = null;

        if (productions.containsKey(token.getTokenType())){
            pair = productions.get(token.getTokenType());
        }
        else{
            for (TokenType.ClusterFeature clusterFeature:
                 token.getTokenType().getClusterFeatures()) {
                if (productions.containsKey(clusterFeature)){
                    pair = productions.get(clusterFeature);
                    break;
                }
            }
        }

        if (pair != null){
            if (pair.getValue1() != null){
                pair.getValue1().handle(token);
            }

            return pair.getValue0();
        }

        return null;
    }

    public boolean canReceiveToken(Token token){
        if (productions.containsKey(token.getTokenType())){
            return true;
        }
        else{
            for (TokenType.ClusterFeature clusterFeature:
                    token.getTokenType().getClusterFeatures()) {
                if (productions.containsKey(clusterFeature)){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return symbolName;
    }
}
