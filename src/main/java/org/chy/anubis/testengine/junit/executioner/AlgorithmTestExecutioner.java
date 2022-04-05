package org.chy.anubis.testengine.junit.executioner;


import org.chy.anubis.dynamic.DynamicRunEngine;

import org.chy.anubis.dynamic.TestCaseExecuterFactory;
import org.chy.anubis.dynamic.template.CtTemplate;
import org.chy.anubis.dynamic.template.CtTemplateContext;
import org.chy.anubis.dynamic.template.TestCaseExecuter;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.log.Logger;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.WarehouseUtils;
import org.junit.platform.engine.EngineExecutionListener;

import java.util.*;
import java.util.stream.Collectors;

import static org.chy.anubis.Constant.ALGORITHM_INTERFACE_NAME;

public class AlgorithmTestExecutioner extends CommonExecutioner<AlgorithmTestDescriptor> {


    private final AlgorithmMethodDefinition definition;
    /**
     * 动态执行器实例
     */
    private final DynamicRunEngine dynamicRunEngine;

    public AlgorithmTestExecutioner(EngineExecutionListener listener, AlgorithmTestDescriptor testDescriptor,
                                    DynamicRunEngine dynamicRunEngine) {

        super(listener, testDescriptor);
        this.definition = testDescriptor.getAlgorithmMethodDefinition();
        this.dynamicRunEngine = dynamicRunEngine;
    }

    public void start() {
        run(this::doStart);
    }

    public void doStart(AlgorithmTestDescriptor algorithmTestDescriptor) {
        Set<CaseTestDescriptor> testChildren = algorithmTestDescriptor.getTestChildren();
        if (testChildren == null || testChildren.isEmpty()) {
            Logger.info("没有获取到该算法下面要执行的测试用例");
            return;
        }
        //获取对应的接口文件
        JavaFile algorithmInterface = LocalCodeManager.instance.getJavaSource(WarehouseUtils.getPathFromAlgorithmPath(definition.getCaseSourceType(),
                definition.getAlgorithmName(), ALGORITHM_INTERFACE_NAME));

        //编译这个并加载这个接口文件
        dynamicRunEngine.loadClass(algorithmInterface);

        String allCaseName = testChildren.stream().map(CaseTestDescriptor::getDisplayName).collect(Collectors.joining(" , "));
        Logger.info("将要执行的测试用例为: [" + allCaseName + "]");

        //获取准备好的 java模版,用来根据上面的 案例模版生成新的 java类
        CtTemplate bootstrapCtTemplate = CtTemplateContext.instance.getAlgorithmTemplate();

        TestCaseExecuterFactory testCaseExecuterFactory = new TestCaseExecuterFactory(dynamicRunEngine);
        //生成 testCase 用例的执行器
        Class<? extends TestCaseExecuter> bootstrapTestCaseExecuter = testCaseExecuterFactory.getBootstrapTestCaseExecuter(StringUtils.humpToLine(algorithmTestDescriptor.getDisplayName()),
                bootstrapCtTemplate, algorithmTestDescriptor.getAlgorithmMethodDefinition().getMethod(), algorithmInterface);

        //遍历这个算法下面所有的测试案例
        algorithmTestDescriptor.foreachChild(caseTestDescriptor -> {
            CaseTestExecutioner executioner = new CaseTestExecutioner(listener, caseTestDescriptor,
                    bootstrapTestCaseExecuter, dynamicRunEngine);
            //开始执行这个测试用例
            executioner.start();
        });
    }

}
