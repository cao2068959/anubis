package org.chy.anubis.dynamic.paramatch;

import lombok.Data;

import java.lang.reflect.Parameter;

@Data
public class MatchResult {

    /**
     * 对比参数本身
     */
    private Parameter parameter;

    /**
     * 匹配到的参数名称
     */
    private String matchName;

    /**
     * 如果需要转换代码 那么转换表达式
     */
    private String convertExpression;

    public MatchResult(Parameter parameter) {
        this.parameter = parameter;
    }
}
