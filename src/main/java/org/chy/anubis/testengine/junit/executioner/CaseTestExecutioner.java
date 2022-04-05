package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.dynamic.DynamicRunEngine;
import org.chy.anubis.dynamic.template.TestCaseExecuter;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.ReflectUtils;
import org.junit.platform.engine.EngineExecutionListener;

import static org.chy.anubis.Constant.TESTCASE_TEMPLATE_CASE;


public class CaseTestExecutioner extends CommonExecutioner<CaseTestDescriptor> {

    private final Class<? extends TestCaseExecuter> testCaseExecuterClass;
    private final DynamicRunEngine dynamicRunEngine;

    public CaseTestExecutioner(EngineExecutionListener listener,
                               CaseTestDescriptor testDescriptor,
                               Class<? extends TestCaseExecuter> testCaseExecuterClass ,DynamicRunEngine dynamicRunEngine) {
        super(listener, testDescriptor);
        this.testCaseExecuterClass = testCaseExecuterClass;
        this.dynamicRunEngine = dynamicRunEngine;
    }


    public void start() {
        run(this::doStart);
    }

    private void doStart(CaseTestDescriptor caseTestDescriptor) {
        CaseBriefInfo caseBriefInfo = caseTestDescriptor.getCaseBriefInfo();

        //获取这个执行案例的模版方法
        JavaFile templateCase = LocalCodeManager.instance.getJavaSource(caseBriefInfo.getUrl() + "/" + TESTCASE_TEMPLATE_CASE);
        Class<?> templateCaseClass = dynamicRunEngine.loadClass(templateCase);
        TestCaseExecuter testCaseExecuterIntance = getTestCaseExecuterIntance(templateCaseClass);

        //运行
        testCaseExecuterIntance.run();
    }

    private TestCaseExecuter getTestCaseExecuterIntance(Class<?> templateCaseClass){
        TestCaseExecuter testCaseExecuter = ReflectUtils.getInstance(testCaseExecuterClass);
        Object templateCase = ReflectUtils.getInstance(templateCaseClass);
        testCaseExecuter.setTestCase(templateCase);
        return testCaseExecuter;
    }

    private AlgorithmTestDescriptor getAlgorithmTestDescriptor() {
        return (AlgorithmTestDescriptor) testDescriptor.getParent().get();
    }

}
