package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.entity.ParameterInfo;

import java.util.List;

public interface IParamMatch {

    /**
     * 用于匹配测试参数的接口
     *
     * @param result 该参数不会为null, 第一个接口开始匹配的时候
     * @param candidateParam 从这堆候选参数中寻找合适的对象,如果找到了请移除对应的参数
     */
    public void match(MatchResult[] result, List<ParameterInfo> candidateParam);

}
