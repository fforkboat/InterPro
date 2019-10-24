package com.fforkboat.common;

import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;

public class ValueTable {
    private List<Variable> variables = new ArrayList<>();
    private List<Double> doubles = new ArrayList<>();
    private List<Integer> integers = new ArrayList<>();
    private List<String> strings = new ArrayList<>();

    private ValueTable(){}
    private static ValueTable valueTable = new ValueTable();
    public static ValueTable getValueTable(){return valueTable;}

    /**
     * 拿到变量在变量表中的位置，如果变量表中没有名称为variableName的变量，先创建它，并添加到变量表中
     * */
    public int getVariableIndex(String variableName){
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = variables.get(i);
            if (variable.getName().equals(variableName))
                return i;
        }
        Variable variable = new Variable(variableName);
        variables.add(variable);
        return variables.size() - 1;
    }

    public Variable getVariable(int index){
        return variables.get(index);
    }

    public int getIntegerIndex(String number){
        Integer num = Integer.valueOf(number);
        if (integers.contains(num))
            return integers.indexOf(num);

        integers.add(num);
        return integers.size() - 1;
    }

    public Integer getInteger(int index){
        return integers.get(index);
    }

    public int getDoubleIndex(String number){
        Double num = Double.valueOf(number);
        if (doubles.contains(num))
            return doubles.indexOf(num);

        doubles.add(num);
        return doubles.size() - 1;
    }

    public Double getDouble(int index){
        return doubles.get(index);
    }

    public int getStringIndex(String string){
        if (strings.contains(string))
            return strings.indexOf(string);

        strings.add(string);
        return strings.size() - 1;
    }

    public String getString(int index){
        return strings.get(index);
    }


}
