package com.blue;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

import com.blue.api.Command;


public class Util {
    public static <X, Y> List<Y> listGetter(Iterable<X> source, Function<X, Y> mapper) {
        List<Y> temp = new ArrayList<>(); 

        for (X item : source)
            temp.add(mapper.apply(item));

        return temp;
    }

    public static boolean getPrefix(String message) {
        return message.startsWith("!");
    }


}
