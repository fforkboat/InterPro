package com.fforkboat.interpreter;

class Number {
    private Object value;

    private Number(){}

    Object getValue(){
        return value;
    }

    static Number createNumber(Object value){
        Number number = new Number();
        if (value instanceof Integer){
            number.value = value;
            return number;
        }
        else if (value instanceof Double){
            number.value = value;
            return number;
        }

        throw new IllegalArgumentException("value is not a number");
    }

    static Number add(Number op1, Number op2){
        Number number = new Number();
        if (op1.value instanceof Integer){
            if (op2.value instanceof Integer){
                number.value = (Integer)op1.value + (Integer)op2.value;
            }
            else {
                number.value = (Integer)op1.value + (Double)op2.value;
            }
        }
        else {
            if (op2.value instanceof Integer){
                number.value = (Double)op1.value + (Integer)op2.value;
            }
            else {
                number.value = (Double)op1.value + (Double)op2.value;
            }
        }

        return number;
    }

    static Number subtract(Number op1, Number op2){
        Number number = new Number();
        if (op1.value instanceof Integer){
            if (op2.value instanceof Integer){
                number.value = (Integer)op1.value - (Integer)op2.value;
            }
            else {
                number.value = (Integer)op1.value - (Double)op2.value;
            }
        }
        else {
            if (op2.value instanceof Integer){
                number.value = (Double)op1.value - (Integer)op2.value;
            }
            else {
                number.value = (Double)op1.value - (Double)op2.value;
            }
        }

        return number;
    }

    static Number multiply(Number op1, Number op2) {
        Number number = new Number();
        if (op1.value instanceof Integer) {
            if (op2.value instanceof Integer) {
                number.value = (Integer) op1.value * (Integer) op2.value;
            } else {
                number.value = (Integer) op1.value * (Double) op2.value;
            }
        } else {
            if (op2.value instanceof Integer) {
                number.value = (Double) op1.value * (Integer) op2.value;
            } else {
                number.value = (Double) op1.value * (Double) op2.value;
            }
        }

        return number;
    }

    static Number divide(Number op1, Number op2) {
        Number number = new Number();
        try {
            if (op1.value instanceof Integer) {
                if (op2.value instanceof Integer) {
                    number.value = (Integer) op1.value / (Integer) op2.value;
                } else {
                    number.value = (Integer) op1.value / (Double) op2.value;
                }
            } else {
                if (op2.value instanceof Integer) {
                    number.value = (Double) op1.value / (Integer) op2.value;
                } else {
                    number.value = (Double) op1.value / (Double) op2.value;
                }
            }
        }catch (ArithmeticException e){
            return null;
        }

        return number;
    }

}
