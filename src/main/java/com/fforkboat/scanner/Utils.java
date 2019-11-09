package com.fforkboat.scanner;

class Utils {
    static boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    static boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }
}
