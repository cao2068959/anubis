package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.annotation.TrialParam;
import org.chy.anubis.entity.ParameterInfo;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

/**
 * 通过名称进行比对
 */
public class NameParamMatch extends AbstractParamMatch {

    @Override
    public ParameterInfo itemMatch(MatchResult matchResult, List<ParameterInfo> candidateParam) {
        Parameter parameter = matchResult.getParameter();
        //字段的名称
        String name = Optional.ofNullable(parameter.getDeclaredAnnotation(TrialParam.class))
                .map(TrialParam::value).orElse(parameter.getName());
        for (ParameterInfo parameterInfo : candidateParam) {
            if (name.equals(parameterInfo.getName())) {
                //设置匹配上的字段
                setMatchResult(matchResult, parameterInfo);
                //配上之后需要移除对应的候选字段
                return parameterInfo;
            }
        }
        return null;
    }
}
