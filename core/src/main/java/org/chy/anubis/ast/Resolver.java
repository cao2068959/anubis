package org.chy.anubis.ast;

import org.chy.anubis.ast.chartool.CharCache;
import org.chy.anubis.ast.chartool.CharGroupLimitMatch;
import org.chy.anubis.ast.chartool.CharGroupMatch;
import org.chy.anubis.ast.chartool.ValidCharGroup;
import org.chy.anubis.enums.CharGroupMatchResult;

import java.util.Optional;

import static org.chy.anubis.enums.CharGroupMatchResult.FAIL;


public class Resolver {
    //当前解析到的位置
    int index;
    boolean isJavaInfoParse = false;
    JavaSourceClassDefinition javaSourceClassDefinition;
    char[] datas;

    public Resolver(JavaSourceClassDefinition javaSourceClassDefinition) {
        this.javaSourceClassDefinition = javaSourceClassDefinition;
        datas = javaSourceClassDefinition.datas;
    }

    //解析
    public void run() {
        //解析package
    }

    /**
     * 解析 package
     */
    public void parseJavaInfo() {
        if (isJavaInfoParse) {
            return;
        }

        while (true) {
            Optional<ValidCharGroup> characters = nextWord(null);
            //解析到文件尾部了
            if (!characters.isPresent()) {
                return;
            }
            ValidCharGroup validCharGroup = characters.get();
            //解析出来是 package开头, 那么那就直接去获取包信息
            if (validCharGroup.equals("package")) {
                String packageContent = nextWord(';')
                        .orElseThrow(() -> new IllegalArgumentException("文件格式错误")).substring(0, -1).toString();
                javaSourceClassDefinition.setPackagePath(packageContent);
            }

            //解析出来是 import开头, 那么那就直接去获取包信息
            if (validCharGroup.equals("import")) {
                String importContent = nextWord(';')
                        .orElseThrow(() -> new IllegalArgumentException("文件格式错误")).substring(0, -1).toString();
                javaSourceClassDefinition.addImportContent(importContent);
            }


            if (validCharGroup.equals("interface")) {
                String className = nextWord(null)
                        .orElseThrow(() -> new IllegalArgumentException("文件格式错误")).substring(0, -1).toString();
                javaSourceClassDefinition.setClassName(className);
                isJavaInfoParse = true;
                return;
            }

        }


    }


    /**
     * 获取下一个 单词, 从当前位置开始, 往后获取,直到获取到结束符为止, 如果文件已经到末尾,那么就获取到文件结尾
     * 如果 endMark 为null 那么将使用空格占位符匹配 这里的空格包括换行等, 及 '\r' '\n'
     * 当使用空格当做结束符的时候, 如果在有效数据之前有连续空格/换行将会被忽略如 \n\n\n\n\n 123 那么将只会返回 123
     * <p>
     * 在获取代码的时候,如果遇到了 单行注释或者多行注解都会把他跳过
     *
     * @return
     */
    public Optional<ValidCharGroup> nextWord(Character endMark) {
        int length = datas.length;
        if (index > length - 1) {
            return Optional.empty();
        }
        //多行注释的匹配器
        CharGroupLimitMatch multiLineNotesMatch = new CharGroupLimitMatch(new CharGroupMatch("/*"), new CharGroupMatch("*/"), false);
        //单行注释的前缀匹配器
        CharGroupLimitMatch lineNotesMatch = new CharGroupLimitMatch(new CharGroupMatch("//"), new CharGroupMatch('\n'), false);


        int startIndex = index;
        boolean validData = false;
        while (index <= length - 1) {
            char currentChar = datas[index];
            index++;

            CharGroupMatchResult multiLineNotesResult = multiLineNotesMatch.match(currentChar);
            CharGroupMatchResult lineNotesResult = lineNotesMatch.match(currentChar);
            //注释的话跳过
            if (multiLineNotesResult != FAIL || lineNotesResult != FAIL) {
                startIndex = index;
                continue;
            }

            if (endMark == null) {
                if (currentChar == ' ' || currentChar == '\r' || currentChar == '\n') {
                    //还没匹配到有效数据
                    if (validData) {
                        break;
                    } else {
                        continue;
                    }
                }
                validData = true;
            } else {
                if (currentChar == endMark) {
                    break;
                }
            }
        }

        ValidCharGroup result = new ValidCharGroup(datas, startIndex, index - 1);
        return Optional.of(result);
    }


}