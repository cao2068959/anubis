package org.chy.anubis.ast.chartool;

import java.util.HashMap;
import java.util.Map;

public class CharCache {
    static Map<String, char[]> datas = new HashMap<>();

    static {
        putData("/*");
        putData("*/");
        putData("//");
        putData("package");
        putData("import");
        putData("interface");
        putData("class");
    }

    private static void putData(String data) {
        datas.put(data, data.toCharArray());
    }

    public static char[] get(String data) {
        char[] result = datas.get(data);
        if (result == null) {
            return data.toCharArray();
        }
        return result;
    }


}
