package org.chy.anubis.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;

public class StringUtils {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     *
     * @param data
     * @return
     */
    public static String humpToLine(String data) {
        StringBuilder result = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (90 >= c && c >= 65) {
                char lowc = (char) (c + 32);
                if (result.length() > 0) {
                    result.append("_");
                }
                result.append(lowc);

            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
