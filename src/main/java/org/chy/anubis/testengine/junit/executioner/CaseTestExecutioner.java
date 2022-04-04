package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.Constant;
import org.chy.anubis.annotation.TrialParam;
import org.chy.anubis.dynamic.DynamicRunEngine;
import org.chy.anubis.dynamic.template.CtTemplate;
import org.chy.anubis.dynamic.template.CtTemplateContext;
import org.chy.anubis.dynamic.template.Executer;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.JavaMethodInfo;
import org.chy.anubis.entity.parameterInfo;
import org.chy.anubis.exception.CompilerException;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.log.Logger;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.TypeUtils;
import org.junit.platform.engine.EngineExecutionListener;

import javax.jws.WebParam;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.chy.anubis.Constant.TESTCASE_TEMPLATE_CASE;


public class CaseTestExecutioner extends CommonExecutioner<CaseTestDescriptor> {

    JavaFile algorithmInterface;
    DynamicRunEngine dynamicRunEngine;

    public CaseTestExecutioner(EngineExecutionListener listener,
                               CaseTestDescriptor testDescriptor, JavaFile algorithmInterface, DynamicRunEngine dynamicRunEngine) {
        super(listener, testDescriptor);
        this.algorithmInterface = algorithmInterface;
        this.dynamicRunEngine = dynamicRunEngine;
    }


    public void start() {
        run(this::doStart);
    }

    private void doStart(CaseTestDescriptor caseTestDescriptor) {
        CaseBriefInfo caseBriefInfo = caseTestDescriptor.getCaseBriefInfo();

        //获取这个执行案例的模版方法
        JavaFile templateCase = LocalCodeManager.instance.getJavaSource(caseBriefInfo.getUrl() + "/" + TESTCASE_TEMPLATE_CASE);
        Class<?> aClass = dynamicRunEngine.loadClass(templateCase);

        //获取准备好的 java模版,用来根据上面的 案例模版生成新的 java类
        CtTemplate bootstrapCtTemplate = CtTemplateContext.instance.getAlgorithmTemplate();

        //去生成真正要去执行的入口类
        genBootstrapClass(bootstrapCtTemplate, templateCase);
        System.out.println(aClass);


    }

    /**
     * 生成真正运行的入口类, 整个算法的运行入口都从这里开始
     */
    private Class<? extends Executer> genBootstrapClass(CtTemplate ctTemplate, JavaFile testCase) {
        genBootstrapJavaSource(ctTemplate, testCase);


        return null;

    }

    /**
     * 生成入口类的java源文件
     */
    private Class<? extends Executer> genBootstrapJavaSource(CtTemplate ctTemplate, JavaFile testCase) {
        String rondom = StringUtils.randomStr(5);
        AlgorithmTestDescriptor algorithmTestDescriptor = getAlgorithmTestDescriptor();
        AlgorithmMethodDefinition algorithmMethodDefinition = algorithmTestDescriptor.getAlgorithmMethodDefinition();

        //本次执行算法的名称
        String algorithmName = algorithmTestDescriptor.getDisplayName();
        algorithmName = StringUtils.humpToLine(algorithmName);
        //测试用例的名称
        String caseName = testDescriptor.getDisplayName();
        //生成包路径
        String packageName = Constant.TREASURY_BASE_PATH + ".bootstrap." + algorithmName + "." + caseName;
        ctTemplate.addParam("packagePath", packageName);
        //生成 className
        String className = caseName + "_" + rondom;
        ctTemplate.addParam("name", className);
        //添加算法接口全路径
        ctTemplate.addParam("algorithmInterface", algorithmInterface.getJavaAllClassName());

        //获取算法接口
        JavaMethodInfo algorithmInterfaceMethod = algorithmInterface.getFirstMethod();
        ctTemplate.addParam("algorithmMethod", algorithmInterfaceMethod.getMethodSignature());
        //用户自己写的方法
        Method testMethod = algorithmMethodDefinition.getMethod();
        ctTemplate.addParam("testMethodName", testMethod.getName());
        ctTemplate.addParam("testInstanceClass", testMethod.getDeclaringClass().getTypeName());
        //对返回值做一些处理
        bootstrapClassReturnHandle(algorithmInterfaceMethod, ctTemplate);
        //参数处理
        bootstrapClassParamsHandle(algorithmInterfaceMethod, testMethod, ctTemplate);


    }


    private void bootstrapClassParamsHandle(@WebParam JavaMethodInfo algorithmInterfaceMethod, Method method, CtTemplate ctTemplate) {
        List<parameterInfo> parameters = algorithmInterfaceMethod.getParameter();
        Parameter[] loacalMethodParameters = method.getParameters();
        List<String> result = new ArrayList<>();

        for (int i = 0; i < loacalMethodParameters.length; i++) {
            //先看需要参数的类型是什么
            Class<?> requireType = loacalMethodParameters[i].getType();
            findRequire(requireType, parameters, result, method, i);

        }

        //参数个数不对,但是不影响使用,提示一下
        if (loacalMethodParameters.length != parameters.size()) {
            Logger.waring("本地算法方法[" + method.getName() + "]参数个数为[" + loacalMethodParameters.length + "] 但远程用例参数个数为 [" + parameters.size() + "], 远程用例参数为[" + formatParam(parameters) + "]");
        }

        //生成对应的参数
        ctTemplate.addParam("testMethodArgs", StringUtils.join("", "", result, ags -> ags));

    }

    /**
     * 通过类型去判断需要的到底是哪一个参数
     * <p>
     * 如果有多个相同的类型, 需要使用 @TrialParam 注解去指定名称
     */
    private void findRequire(Class<?> requireType, List<parameterInfo> parameters,
                             List<String> result, Method method, int index) {
        String name = "";
        TrialParam trialParamAnnotation = requireType.getDeclaredAnnotation(TrialParam.class);
        if (trialParamAnnotation != null) {
            name = trialParamAnnotation.value();
        }
        String typeName = requireType.getTypeName();
        parameterInfo candidate = null;
        for (parameterInfo parameter : parameters) {
            //先匹配类型是否相同
            if (!TypeUtils.typeMatch(typeName, parameter.getType())) {
                continue;
            }
            //类型匹配上,并且名称也是相同的,那么就算找到了
            if (name.equals(parameter.getName())) {
                candidate = parameter;
                break;
            }
            //类型匹配上了,但是没指定名称, 如果没有相同的类型也匹配上那么也算通过
            //匹配上了相同的类型
            if (candidate != null) {
                throw new CompilerException("本地方法 [" + method.getName() + "] 的第[" + index + "] 个参数, 无法确认和远程方法的参数映射关系, 因为远程方法中存在多个类型一样的参数," +
                        "请使用注解 @TrialParam 来指定要映射的参数名称, 远程方法参数为: " + formatParam(parameters));
            }
            candidate = parameter;
        }

        if (candidate == null) {
            throw new CompilerException("本地方法 [" + method.getName() + "] 的第[" + index + "] 个参数 无法在远程方法中找到要映射的参数 远程方法参数为:" + formatParam(parameters));

        }

        String candidateName = candidate.getName();

        for (int i = 0; i < result.size(); i++) {
            String useName = result.get(i);
            if (useName.equals(candidateName)) {
                throw new CompilerException("本地方法 [" + method.getName() + "] 的第[" + index + "] 个参数 以及 第[" + i + "] 参数使用了同一个远程方法参数 参数名称[" + candidateName + "],  远程方法参数为: " + formatParam(parameters));
            }
        }
        result.add(candidateName);
    }

    private String formatParam(List<parameterInfo> parameters) {
        return StringUtils.join("(", ")", parameters,
                parameterInfo -> parameterInfo.getType() + " " + parameterInfo.getName());
    }

    private void bootstrapClassReturnHandle(JavaMethodInfo algorithmInterfaceMethod, CtTemplate ctTemplate) {
        Optional<String> returnType = algorithmInterfaceMethod.getReturnType();
        if (!returnType.isPresent()) {
            ctTemplate.addParam("testMethodReturn", "");
            ctTemplate.addParam("testMethodReturnFlag", "");
            ctTemplate.addParam("resultReturn", "");
            return;
        }

        ctTemplate.addParam("testMethodReturn", returnType.get());
        ctTemplate.addParam("testMethodReturnFlag", " result = ");
        ctTemplate.addParam("resultReturn", "return result");
    }

    private AlgorithmTestDescriptor getAlgorithmTestDescriptor() {
        return (AlgorithmTestDescriptor) testDescriptor.getParent().get();
    }


}
