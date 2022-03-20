package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.Constant;
import org.chy.anubis.dynamic.DynamicRunEngine;
import org.chy.anubis.dynamic.template.CtTemplate;
import org.chy.anubis.dynamic.template.CtTemplateContext;
import org.chy.anubis.dynamic.template.Executer;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.JavaMethodInfo;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.StringUtils;
import org.junit.platform.engine.EngineExecutionListener;

import java.lang.reflect.Method;
import java.util.Optional;

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



        return null;

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
