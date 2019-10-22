package com.fforkboat.scanner;

import java.util.HashMap;
import java.util.Map;

/**
 * “广义”的一个字符
 * */
class GeneralChar{
    private String generalChar;
    static final String ANY = "$.$";
    static final String DIGIT = "$d$";
    static final String LETTER = "$l$";
    static final String IDENTIFIER = "$i$";

    private static Map<String, GeneralChar> pool = new HashMap<>();
    static {
        pool.put("$.$", new GeneralChar("$.$"));
        pool.put("$d$", new GeneralChar("$d$"));
        pool.put("$l$", new GeneralChar("$l$"));
        pool.put("$i$", new GeneralChar("$i$"));
    }

    private GeneralChar(String generalChar){ this.generalChar = generalChar; }
    GeneralChar createGeneralChar(String generalChar){
        if (pool.containsKey(generalChar))
            return pool.get(generalChar);

        GeneralChar gc = new GeneralChar(generalChar);
        pool.put(generalChar, gc);
        return gc;
    }

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