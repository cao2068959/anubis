package org.chy.anubis.utils;

import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("all")
public class PlaceholderUtils {

    /**
     * 替换占位符,占位符最高不能超过 30 char
     * <p>
     * 这里没用 StringBuilder 去拼接的原因在于，因为我是逐字扫描的，每次append都伴随 字符数组的扩容
     *
     * @param target 目标字符串
     * @param params 要替换的占位符的数据  key: 占位符 value : 要替换的值
     * @param prefix 占位符的开始标识
     * @param suffix 占位符的结束标识
     * @return
     */
    public static String replacePlaceholder(String target, Map<String, String> params, String prefix, String suffix) {
        char[] targetChars = target.toCharArray();
        int targetCharsLen = targetChars.length;
        //因为 仅仅只是 替换了部分的占位符，其实2个字符串的大小不会有太大的差别，如果不够了后面再去扩容
        SmartCharArray resultChars = new SmartCharArray(targetCharsLen);

        SmartCharArray prefixChars = new SmartCharArray(prefix.toCharArray());
        SmartCharArray suffixChars = new SmartCharArray(suffix.toCharArray());

        //疑似占位符，最大只能 30 char
        SmartCharArray mayPlaceholder = new SmartCharArray(30 + suffixChars.length());

        int prefixIndex = -1;

        for (int i = 0; i < targetCharsLen; i++) {
            char current = targetChars[i];
            // 如果 prefixIndex > 0 说明 可能进入了 占位符的 区域
            if (prefixIndex >= 0) {
                //把占位符放入 mayPlaceholder 中， 如果 mayPlaceholder 满了也没匹配到结束符号，说明 之前那个并不是前置占位符，仅仅只是误判
                //返回 false 说明 mayPlaceholder 满了，去清空所有的标识，当做什么事情都没发生过
                if (!mayPlaceholder.append(current)) {
                    resultChars.append(prefix);
                    i = prefixIndex;
                    prefixIndex = -1;
                    mayPlaceholder.clean();
                    continue;
                }
                //这里匹配上说明已经成功的匹配上了 结束符号
                if (suffixChars.compare(current)) {
                    //因为mayPlaceholder 把后缀也给保存进去了，这里把后缀给排除
                    mayPlaceholder.cleanlast(suffixChars.length());
                    //获取占位符
                    String placeholder = mayPlaceholder.toString();
                    //获取这个占位符对应的值
                    String value = params.get(placeholder);
                    if (value == null) {
                        throw new RuntimeException("覆盖模板找不到占位符 " + value + "对应的值");
                    }
                    //把占位符对应的值放入进去
                    resultChars.append(value);
                    prefixIndex = -1;
                    mayPlaceholder.clean();
                    continue;
                }
            } else {
                //正常把每一个字节给复制进去，如果这里返回false 就是 resultChars 满了，进行扩容
                if (!resultChars.append(current)) {
                    resultChars.dilatancy(targetCharsLen - i + 10);
                    resultChars.append(current);
                }
                //这里一旦 比较通过了就说明已经成功匹配上前置占位符了
                if (prefixChars.compare(current)) {
                    prefixChars.clean();
                    //把匹配到的前置占位符的 下标给记录一下
                    prefixIndex = i;
                    resultChars.cleanlast(prefixChars.length());
                }
            }
        }
        return resultChars.toString();
    }

    static class SmartCharArray {
        char[] chars;
        int compareIndex = 0;
        int index = 0;

        SmartCharArray(char[] chars) {
            this.chars = chars;
        }

        SmartCharArray(int len) {
            this.chars = new char[len];
        }

        boolean append(char c) {
            //越界了
            if (index >= chars.length) {
                return false;
            }
            chars[index] = c;
            index++;
            return true;
        }

        /**
         * 拼接 String 越界了会自动扩容
         *
         * @param value
         * @return
         */
        public boolean append(String value) {
            char[] valueChars = value.toCharArray();
            for (char valueChar : valueChars) {
                if (!append(valueChar)) {
                    //扩容
                    dilatancy(valueChars.length);
                    append(valueChar);
                }
            }
            return true;
        }


        /**
         * 连续比较，连续传入 的char 和对象内持有的 chars 比较
         *
         * @param value
         * @return
         */
        public boolean compare(char value) {
            if (value != chars[compareIndex]) {
                compareIndex = 0;
                return false;
            }

            if (compareIndex == chars.length - 1) {
                return true;
            }

            compareIndex++;
            return false;
        }

        /**
         * 扩容
         *
         * @param num 要扩容的大小
         */
        public void dilatancy(int num) {
            int newlen = chars.length + num;
            chars = Arrays.copyOf(chars, newlen);
        }

        public void clean() {
            compareIndex = 0;
            index = 0;
        }

        public void cleanlast(int num) {
            index = index - num;
        }

        public int length() {
            return chars.length;
        }

        @Override
        public String toString() {
            return String.valueOf(Arrays.copyOf(chars, index));
        }

    }

}
