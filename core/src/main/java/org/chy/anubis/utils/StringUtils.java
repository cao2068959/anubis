package org.chy.anubis.utils;

import org.chy.anubis.entity.Pair;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;

public class StringUtils {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    private static char[] allChar = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


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


    public static Pair<String, String> separatePath(String path, String separateChar) {
        if (path == null || "".equals(path)) {
            return Pair.of(separateChar, null);
        }
        int index = path.lastIndexOf(separateChar);
        String filePath = path.substring(0, index);
        String fileName = path.substring(index + 1);
        return Pair.of(filePath, fileName);
    }

    public static <T> String join(String prefix, String suffix, Iterable<T> datas, Function<T, String> findData) {
        StringBuilder result = new StringBuilder(prefix);
        boolean first = true;
        for (T data : datas) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            String stringData = findData.apply(data);
            result.append(stringData);
        }

        result.append(suffix);
        return result.toString();
    }


    /**
     * base64 解密
     *
     * @param blobData
     */
    public static String base64Decode(String blobData) {
        if (blobData == null) {
            return "";
        }
        return new String(Base64.getDecoder().decode(blobData), StandardCharsets.UTF_8);
    }

    /**
     * 随机生成指定位数的字符串
     */
    public static String randomStr(int i) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        int bound = allChar.length - 1;
        int index = i;
        while (index != 0) {
            result.append(allChar[random.nextInt(bound)]);
            index--;
        }
        return result.toString();
    }
}
