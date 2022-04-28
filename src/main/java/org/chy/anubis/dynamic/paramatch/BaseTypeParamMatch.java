package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.entity.ParameterInfo;
import org.chy.anubis.utils.TypeUtils;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 基础数据类型的匹配, 类型完全一致就行, 但是如果有多个参数是相同类型, 则不行需要使用 @TrialParam 注解来指定
 */
public class BaseTypeParamMatch extends AbstractParamMatch {

    @Override
    public ParameterInfo itemMatch(MatchResult matchResult, List<ParameterInfo> candidateParam) {
        Parameter parameter = matchResult.getParameter();
        Class<?> type = parameter.getType();

        ParameterInfo will = null;
        for (ParameterInfo parameterInfo : candidateParam) {
            if (TypeUtils.typeMatch(parameterInfo.getType(), type.getTypeName())){
                //有相同类型, 还是算了
                if (will != null){
                    return null;
                }
                will = parameterInfo;
            }
        }
        if (will != null){
            matchResult.setMatchName(will.getName());
        }
        return will;
    }
}
