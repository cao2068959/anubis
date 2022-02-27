package org.chy.anubis.exception;

import com.github.javaparser.Problem;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.utils.StringUtils;

import java.util.List;

public class JavaParserException extends RuntimeException {


    public JavaParserException(String message) {
        super(message);
    }

    public JavaParserException(List<Problem> problems) {
        super("文件解析失败: " + handleErrorList(problems));
    }

    private static String handleErrorList(List<Problem> problems) {
        return StringUtils.join("[", "]", problems, Problem::toString);
    }




}
