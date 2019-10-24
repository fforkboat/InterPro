package com.fforkboat.scanner.token;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 所有token类的抽象基类
 * 该类是不可变的
 * */
public abstract class Token {
    private TokenType tokenType;
    private int lineIndexOfSourceProgram;
    private List<ClusterFeature> clusterFeatures = new ArrayList<>();

    protected Token(TokenType tokenType, int lineIndex){
        this.tokenType = tokenType;
    }
    protected Token(TokenType tokenType, int lineIndex, ClusterFeature[] clusterFeatures){
        this(tokenType, lineIndex);

        this.clusterFeatures.addAll(Arrays.asList(clusterFeatures));
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    public int getLineIndexOfSourceProgram() {
        return lineIndexOfSourceProgram;
    }

    public boolean hasClusterFeature(ClusterFeature feature){
        return clusterFeatures.contains(feature);
    }

    @Override
    public String toString() {
        return "<" + tokenType.name() + ">";
    }
}
