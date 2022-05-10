package org.chy.anubis.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils {

    public static  <T> List<T> to(T... ts) {
        if (ts == null || ts.length == 0) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(ts.length);
        result.addAll(Arrays.asList(ts));
        return result;
    }
}
