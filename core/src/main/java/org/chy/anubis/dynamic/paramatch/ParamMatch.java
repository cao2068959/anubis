package org.chy.anubis.dynamic.paramatch;


import org.chy.anubis.entity.ParameterInfo;
import org.chy.anubis.exception.CompilerException;
import org.chy.anubis.log.Logger;
import org.chy.anubis.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 方法参数的匹配器, 使用责任链的方式处理
 */
public class ParamMatch {

    private static List<IParamMatch> matchPipline = new ArrayList<>();

    static {
        matchPipline.add(new NameParamMatch());
        matchPipline.add(new BaseTypeParamMatch());
        matchPipline.add(new DimParamMatch());
    }

    /**
     * 从 candidateParam中找到合适的字段转成 targetMethod 能运行的参数
     * 匹配的规则, 根据顺序有优先级
     * 1. 名字相同则则直接匹配, 类型不同的情况下进行转换处理(因为编译的时候字段名称可能会丢失, 可以使用 @TrialParam 注解来指定名称)
     * 2. 使用类型进行匹配, 只能匹配基本类型, 如果需要转换的类型则不能处理如 TreeNode 类型
     * 3. 对于需要转换的类型,使用模糊匹配的方式去指定, 如: 本地为 com.chy.TreeNode 远程为 org.niubi.TreeNode 虽然包名不同,但还是当做是相同类型处理
     *
     * @return 转换后的映射关系, list的顺序就是方法参数的顺序, ParamMappingBO 中包含了转换表达式, 新参数的名称等
     */
    public static List<ParamMappingBO> match(Method targetMethod, List<ParameterInfo> candidateParam) {
        Parameter[] targetParam = targetMethod.getParameters();
        if (targetParam.length != candidateParam.size()) {
            throw new CompilerException("本地方法 [" + targetMethod.getName() + "] 和测试用例接口参数不匹配, 远程接口参数为:" + formatParam(candidateParam));
        }


        MatchResult[] matchResults = Arrays.stream(targetParam).map(MatchResult::new).toArray(MatchResult[]::new);
        //等下这个列表会动态变化, 搞一个副本
        List<ParameterInfo> copyCandidateParam = new LinkedList<>(candidateParam);
        for (IParamMatch iParamMatch : matchPipline) {
            if (copyCandidateParam.size() == 0) {
                break;
            }
            iParamMatch.match(matchResults, copyCandidateParam);
        }

        List<ParamMappingBO> result = new ArrayList<>();
        for (int i = 0; i < matchResults.length; i++) {
            MatchResult matchResult = matchResults[i];
            ParamMappingBO paramMappingBO = new ParamMappingBO();


            String matchName = matchResult.getMatchName();
            if (matchName == null) {
                Logger.waring("远程接口参数为:" + formatParam(candidateParam));
                throw new CompilerException("本地方法 [" + targetMethod.getName() + "] 的第[" + i + "]个参数 无法和测试用例的接口相匹配, 请检查类型是否正确,或使用注解 @TrialParam 来指定要映射的参数名称");
            }
            paramMappingBO.setNewParamName(matchName);
            paramMappingBO.setConvertExpression(matchResult.getConvertExpression());
            paramMappingBO.setParameter(matchResult.getParameter());
            paramMappingBO.setParameterInfo(matchResult.getParameterInfo());
            result.add(paramMappingBO);
        }
        return result;
    }

    private static String formatParam(List<ParameterInfo> parameters) {
        return StringUtils.join("(", ")", parameters,
                ParameterInfo -> ParameterInfo.getType() + " " + ParameterInfo.getName());
    }

}
