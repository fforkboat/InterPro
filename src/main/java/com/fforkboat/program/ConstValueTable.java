package com.fforkboat.program;

import java.util.ArrayList;
import java.util.List;

public class ConstValueTable {
    private List<Double> doubles = new ArrayList<>();
    private List<Integer> integers = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private List<Boolean> bools = new ArrayList<>();

    private ConstValueTable(){}
    private static ConstValueTable constValueTable = new ConstValueTable();
    public static ConstValueTable getConstValueTable(){return constValueTable;}

    /**
     * 拿到整形常量在常量表中的位置，如果常量表中没有，先创建它，并添加到常量表中
     * */
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

    public int getBoolIndex(String string){
        Boolean bool = Boolean.valueOf(string);
        if (bools.contains(bool))
            return bools.indexOf(bool);

        bools.add(bool);
        return bools.size() - 1;
    }

    public Boolean getBool(int index){
        return bools.get(index);
    }
}
