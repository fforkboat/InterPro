package com.fforkboat.scanner;

import java.util.HashMap;
import java.util.Map;

/**
 * 指示一个状态可以识别的“广义”上的字符。一个“广义”字符可以对应一或多个具体字符。
 *
 * 如果一个状态可以识别任意一个字母，在不使用“广义字符”时，需要在该状态的状态转移信息中为每一个字母都添加一个转移情况，非常麻烦
 * 此时如果用广义字符$l$表示任意一个字母，就可以只用添加一个转移情况
 * */
class GeneralChar{
    private static final String ANY = "$.$";
    private static final String DIGIT = "$d$";
    private static final String LETTER = "$l$";
    private static final String IDENTIFIER = "$i$";

    private String generalChar;

    // general char的缓存池，因为GeneralChar是不可变的，所以可以放心复用
    private static Map<String, GeneralChar> pool = new HashMap<>();
    static {
        pool.put("$.$", new GeneralChar(ANY));
        pool.put("$d$", new GeneralChar(DIGIT));
        pool.put("$l$", new GeneralChar(LETTER));
        pool.put("$i$", new GeneralChar(IDENTIFIER));
    }

    private GeneralChar(String generalChar){ this.generalChar = generalChar; }

    /**
     * 静态工厂方法
     * */
    static GeneralChar createGeneralChar(String generalChar){
        if (pool.containsKey(generalChar))
            return pool.get(generalChar);

        GeneralChar gc = new GeneralChar(generalChar);
        pool.put(generalChar, gc);
        return gc;
    }

    /**
     * 判断一个general char能否识别一个具体的char
     * */
    boolean canReceive(char c){
        switch (generalChar) {
            case "$.$":
                return c != '"';
            case "$d$":
                return Utils.isDigit(c);
            case "$l$":
                return Utils.isLetter(c);
            case "$i$":
                return Utils.isLetter(c) || Utils.isDigit(c) || c == '_';
            default:
                return generalChar.equals(String.valueOf(c));
        }
    }
}