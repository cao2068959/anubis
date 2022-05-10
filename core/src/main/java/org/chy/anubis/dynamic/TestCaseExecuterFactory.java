package org.chy.anubis.dynamic;

import org.chy.anubis.Constant;
import org.chy.anubis.dynamic.paramatch.ParamMatch;
import org.chy.anubis.dynamic.template.CtTemplate;
import org.chy.anubis.dynamic.template.TestCaseExecuter;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.JavaMethodInfo;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.entity.ParameterInfo;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.utils.ReflectUtils;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.TypeUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class TestCaseExecuterFactory {

    DynamicRunEngine dynamicRunEngine;

    public TestCaseExecuterFactory(DynamicRunEngine dynamicRunEngine) {
        this.dynamicRunEngine = dynamicRunEngine;
    }

    /**
     * 生成真正运行的入口类, 整个算法的运行入口都从 TestCaseExecuter 对象开始
     *
     * @param algorithmName      算法的名称
     * @param ctTemplate         使用的模版
     * @param testMethod         用户自己写的本地算法方法
     * @param algorithmInterface 远程的算法接口源码
     * @return
     */
    public Class<? extends TestCaseExecuter> getBootstrapTestCaseExecuter(String algorithmName, CtTemplate ctTemplate, Method testMethod,
                                                                          JavaFile algorithmInterface) {
        return genBootstrapClass(algorithmName, ctTemplate, testMethod, algorithmInterface);
    }

    private TestCaseExecuter genBootstrapInstance(Class<? extends TestCaseExecuter> bootstrapClass) {
        //先实例化 bootstrapClass
        return ReflectUtils.getInstance(bootstrapClass);
    }


    private Class<? extends TestCaseExecuter> genBootstrapClass(String algorithmName, CtTemplate ctTemplate, Method testMethod,
                                                                JavaFile algorithmInterface) {
        String bootstrapFilePath = "bootstrap." + algorithmName;

        String fileName = "AlgorithmTemplate_" + algorithmName;
        String filePath = (bootstrapFilePath + "." + fileName).replace(".", "/");
        JavaFile bootstrapJavaFile = (JavaFile) LocalCodeManager.instance.getCacheFileOrLoad(filePath + ".java", path -> {
            //生成包路径
            String packageName = Constant.TREASURY_BASE_PATH + "." + bootstrapFilePath;
            return Optional.of(genBootstrapJavaSource(algorithmName, packageName, ctTemplate, testMethod, algorithmInterface));
        }).orElseThrow(() -> new RuntimeException("生成 BootstrapClass 异常"));
        //开始编译
        return (Class<? extends TestCaseExecuter>) dynamicRunEngine.loadClass(bootstrapJavaFile);
    }

    /**
     * 生成入口类的java源文件
     */
    private String genBootstrapJavaSource(String algorithmName, String packageName,
                                          CtTemplate ctTemplate, Method testMethod,
                                          JavaFile algorithmInterface) {

        //本次执行算法的名称
        algorithmName = StringUtils.humpToLine(algorithmName);
        ctTemplate.addParam("packagePath", packageName);
        //生成 className
        ctTemplate.addParam("name", algorithmName);
        //添加算法接口全路径
        ctTemplate.addParam("algorithmInterface", algorithmInterface.getJavaAllClassName());


        ctTemplate.addParam("imports", String.join("\n", algorithmInterface.getImports()));

        //获取算法接口
        JavaMethodInfo algorithmInterfaceMethod = algorithmInterface.getFirstMethod();
        ctTemplate.addParam("algorithmMethod", algorithmInterfaceMethod.getMethodSignature());

        ctTemplate.addParam("testMethodName", testMethod.getName());
        ctTemplate.addParam("testInstanceClass", testMethod.getDeclaringClass().getTypeName());
        //对返回值做一些处理
        bootstrapClassReturnHandle(algorithmInterfaceMethod, testMethod, ctTemplate);
        //参数处理
        bootstrapClassParamsHandle(algorithmInterfaceMethod, testMethod, ctTemplate);

        //执行模版生成对应的新java源码
        return ctTemplate.executeTemplate();
    }


    private void bootstrapClassParamsHandle(JavaMethodInfo algorithmInterfaceMethod, Method method, CtTemplate ctTemplate) {
        List<ParameterInfo> parameters = algorithmInterfaceMethod.getParameter();

        Pair<String, String> match = ParamMatch.match(method, parameters);

        //生成对应的参数
        ctTemplate.addParam("testMethodArgs", match.getKey());
        ctTemplate.addParam("testMethodArgsConvert", match.getValue());
    }


    private String formatParam(List<ParameterInfo> parameters) {
        return StringUtils.join("(", ")", parameters,
                ParameterInfo -> ParameterInfo.getType() + " " + ParameterInfo.getName());
    }

    private void bootstrapClassReturnHandle(JavaMethodInfo algorithmInterfaceMethod, Method testMethod, CtTemplate ctTemplate) {
        Optional<String> returnType = algorithmInterfaceMethod.getReturnType();
        Class<?> testMethodReturnType = testMethod.getReturnType();
        if (!returnType.isPresent() || testMethodReturnType == Void.class) {
            ctTemplate.addParam("testMethodReturn", "");
            ctTemplate.addParam("testMethodReturnFlag", "");
            ctTemplate.addParam("resultReturn", "");
            ctTemplate.addParam("resultConvert", "");
            return;
        }
        String testMethodResultName = "testMethodResult";
        String resultName = "result";

        ctTemplate.addParam("testMethodReturn", testMethodReturnType.getTypeName());
        ctTemplate.addParam("testMethodReturnFlag", " " + testMethodResultName + " = ");
        //对本地方法的结果进行转换
        String expression = TypeUtils.genConvertExpression(testMethodResultName, returnType.get(), resultName);
        ctTemplate.addParam("resultConvert", expression);
        ctTemplate.addParam("resultReturn", "return " + resultName + ";");
    }


}
