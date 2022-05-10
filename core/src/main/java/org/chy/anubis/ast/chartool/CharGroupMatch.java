package org.chy.anubis.ast.chartool;

import org.chy.anubis.enums.CharGroupMatchResult;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 范围匹配器, 可以用于匹配2个指定标识符之间的数据
 */
public class CharGroupMatch {

    char[] data;
    int index = 0;
    int len = 0;
    boolean success = false;

    public CharGroupMatch(String target) {
        this.data = CharCache.get(target);
        len = data.length;
    }

    public CharGroupMatch(char[] data) {
        this.data = data;
        len = data.length;
    }

    public CharGroupMatch(char data) {
        this.data = new char[]{data};
        len = 1;
    }

    public CharGroupMatchResult match(char target) {
        return match(target, true);
    }

    public CharGroupMatchResult match(char target, boolean autoReset) {
        char c = data[index];
        //匹配上了
        if (c == target) {
            //如果所有的字符都匹配上了,那么返回成功
            if (index == len - 1) {
                success = true;
                if (autoReset) {
                    reset();
                }
                return CharGroupMatchResult.SUCCESS;
            }
            //没有匹配完, 指针后移
            index++;
            return CharGroupMatchResult.MATCHING;
        }
        if (autoReset) {
            reset();
        }
        return CharGroupMatchResult.FAIL;
    }

    public void matchContent(Consumer<Character> consumer) {
        int matchIndex;
        if (success) {
            matchIndex = index;
        } else {
            matchIndex = index - 1;
        }

        for (int i = 0; i < matchIndex; i++) {
            consumer.accept(data[matchIndex]);
        }
    }


    public void reset() {
        index = 0;
        success = false;
    }


}
