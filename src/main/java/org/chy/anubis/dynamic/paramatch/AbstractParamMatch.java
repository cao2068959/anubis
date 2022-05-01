package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.entity.ParameterInfo;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.TypeUtils;

import java.lang.reflect.Parameter;
import java.util.List;

public abstract class AbstractParamMatch implements IParamMatch {

    @Override
    public void match(MatchResult[] result, List<ParameterInfo> candidateParam) {

        for (MatchResult matchResult : result) {
            //该参数已经匹配过了,就不处理了
            if (matchResult.getMatchName() != null) {
                continue;
            }
            ParameterInfo parameterInfo = itemMatch(matchResult, candidateParam);
            if (parameterInfo == null) {
                continue;
            }
            //已经匹配过的就移除
            candidateParam.remove(parameterInfo);
        }

    }

    public void setMatchResult(MatchResult matchResult, ParameterInfo parameterInfo) {
        Parameter parameter = matchResult.getParameter();
        //类型匹配成功的, 直接复制就行
        if (TypeUtils.typeMatch(parameter.getType().getTypeName(), parameterInfo.getType())) {
            matchResult.setMatchName(parameterInfo.getName());
            return;
        }
        //生成的新变量名
        String name = parameterInfo.getName() + "$" + StringUtils.randomStr(4);
        //需要添加类型转换表达式
        String expression = TypeUtils.genConvertExpression(parameterInfo.getName(), parameter.getType().getTypeName(), name);
        matchResult.setMatchName(name);
        matchResult.setConvertExpression(expression);
    }


    public abstract ParameterInfo itemMatch(MatchResult matchResult, List<ParameterInfo> candidateParam);

}
