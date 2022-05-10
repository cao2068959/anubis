package org.chy.anubis.ast.chartool;

import org.chy.anubis.enums.CharGroupMatchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 范围匹配器, 可以用于匹配2个指定标识符之间的数据
 */
public class CharGroupLimitMatch {

    CharGroupMatch prefix;
    CharGroupMatch suffix;

    boolean preMatch = false;

    List<Character> content;


    public CharGroupLimitMatch(CharGroupMatch prefix, CharGroupMatch suffix, boolean saveContent) {
        this.prefix = prefix;
        this.suffix = suffix;
        //是否保存匹配到的内容
        if (saveContent) {
            content = new ArrayList<>();
        }
    }

    public CharGroupMatchResult match(char data) {
        //前缀还没匹配那么开始匹配前缀
        if (!preMatch) {
            CharGroupMatchResult result = prefix.match(data);
            //前缀匹配失败/正在匹配中
            if (result == CharGroupMatchResult.FAIL || result == CharGroupMatchResult.MATCHING) {
                return result;
            }
            //前缀已经完全匹配成功了
            if (result == CharGroupMatchResult.SUCCESS) {
                preMatch = true;
                return CharGroupMatchResult.MATCHING;
            }
        }

        //前缀匹配成功后,开始去匹配中间的内容
        CharGroupMatchResult result = suffix.match(data, false);
        //后缀完全没匹配上, 那么如果需要收集匹配的内容就把后缀匹配的内容放进去.
        if (result == CharGroupMatchResult.FAIL) {
            if (content != null) {
                suffix.matchContent(content::add);
            }
            suffix.reset();
        }

        if (result == CharGroupMatchResult.SUCCESS) {
            reset();
        }
        return CharGroupMatchResult.MATCHING;
    }


    public void reset() {
        content = new ArrayList<>();
        prefix.reset();
        suffix.reset();
        preMatch = false;
    }

}
