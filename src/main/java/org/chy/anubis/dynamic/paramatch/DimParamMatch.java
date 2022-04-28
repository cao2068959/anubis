package org.chy.anubis.dynamic.paramatch;

import org.chy.anubis.entity.ParameterInfo;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 对于需要转换的类型,使用模糊匹配的方式去指定, 如: 本地为 com.chy.TreeNode 远程为 org.niubi.TreeNode 虽然包名不同,但还是当做是相同类型处理
 */
public class DimParamMatch extends AbstractParamMatch {
    @Override
    public ParameterInfo itemMatch(MatchResult matchResult, List<ParameterInfo> candidateParam) {
        Parameter parameter = matchResult.getParameter();
        Class<?> type = parameter.getType();
        String simpleName = type.getSimpleName();

        for (ParameterInfo parameterInfo : candidateParam) {
            if (simpleName.equals(parameterInfo.getSimpleType())) {
                setMatchResult(matchResult, parameterInfo);
                return parameterInfo;
            }
        }
        return null;
    }
}
