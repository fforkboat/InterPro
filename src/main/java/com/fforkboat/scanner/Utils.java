package com.fforkboat.scanner;

public class Utils {
    public static boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }
}
