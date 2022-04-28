package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.entity.Pair;
import org.chy.anubis.entity.ParameterInfo;
import org.chy.anubis.utils.PlaceholderUtils;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.TypeUtils;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParamMatch implements IParamMatch {

    String converTemplate = "${converType} ${newIntance} = org.chy.anubis.utils.TypeUtils.convert(${convertValue}, ${converType}.class);";

    @Override
    public void match(MatchResult[] result, List<ParameterInfo> candidateParam) {

        for (MatchResult matchResult : result) {
            //该参数已经匹配过了,就不处理了
            if (matchResult.getMatchName() != null) {
                continue;
            }
            ParameterInfo parameterInfo = itemMatch(matchResult, candidateParam);
            if (parameterInfo == null){
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
        //需要添加类型转换表达式
        Pair<String, String> nameAndExpression = genConvertExpression(matchResult.getParameter(), parameterInfo);
        matchResult.setMatchName(nameAndExpression.getKey());
        matchResult.setConvertExpression(nameAndExpression.getValue());
    }

    /**
     * 生成对应的通用转换表达式
     *
     * @return left: 新生成变量的名称, right:对应的转换表达式
     */
    private Pair<String, String> genConvertExpression(Parameter parameter, ParameterInfo parameterInfo) {
        Map<String, String> params = new HashMap<>();
        //生成的新变量名
        String name = parameterInfo.getName() + "$" + StringUtils.randomStr(4);
        Class<?> targetType = parameter.getType();
        params.put("newIntance", name);
        params.put("converType", targetType.getTypeName());
        params.put("convertValue", parameterInfo.getName());
        String expression = PlaceholderUtils.replacePlaceholder(converTemplate, params, "${", "}");
        return Pair.of(name, expression);
    }

    public abstract ParameterInfo itemMatch(MatchResult matchResult, List<ParameterInfo> candidateParam);

}
