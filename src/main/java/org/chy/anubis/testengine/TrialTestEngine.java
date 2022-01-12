package org.chy.anubis.testengine;

import org.chy.anubis.testengine.descriptor.TrialClassTestDescriptor;
import org.chy.anubis.testengine.descriptor.TrialMethodTestDescriptor;
import org.chy.anubis.testengine.descriptor.TrialRootTestDescriptor;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.reporting.ReportEntry;

import java.lang.reflect.Method;
import java.util.List;

public class TrialTestEngine implements TestEngine {

    @Override
    public String getId() {
        return "trialTestEngine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        TrialRootTestDescriptor result = new TrialRootTestDescriptor("TrialRootTestDescriptor");
        //获取测试需要测试的类,这里只能获取一个
        List<ClassSelector> classSelectors = discoveryRequest.getSelectorsByType(ClassSelector.class);
        if (!classSelectors.isEmpty()) {
            result.addChild(new TrialClassTestDescriptor(classSelectors.get(0).getJavaClass()));
            return result;
        }
        //如果没有拿到class说明可能是直接去运行的方法
        TrialClassTestDescriptor classTestDescriptor = new TrialClassTestDescriptor("trialTestGroupDescriptor");
        result.addChild(classTestDescriptor);
        List<MethodSelector> methodSelectors = discoveryRequest.getSelectorsByType(MethodSelector.class);
        for (MethodSelector methodSelector : methodSelectors) {
            Method javaMethod = methodSelector.getJavaMethod();
            TrialMethodTestDescriptor.checkAndBuild(javaMethod).ifPresent(classTestDescriptor::addChild);
        }
        return result;
    }

    @Override
    public void execute(ExecutionRequest request) {
        TestDescriptor rootTestDescriptor = request.getRootTestDescriptor();
        if (!(rootTestDescriptor instanceof TrialRootTestDescriptor)) {
            return;
        }


        TrialRootTestDescriptor trialTestGroupDescriptor = (TrialRootTestDescriptor) rootTestDescriptor;
        trialTestGroupDescriptor.foreach(testMethod -> {

            request.getEngineExecutionListener().executionStarted(testMethod);
            request.getEngineExecutionListener().reportingEntryPublished(testMethod, ReportEntry.from("231321",testMethod.getDisplayName()));
            testMethod.foreach(tt -> {
                request.getEngineExecutionListener().executionStarted(tt);

                request.getEngineExecutionListener().reportingEntryPublished(tt, ReportEntry.from("231321",tt.getDisplayName()));
                request.getEngineExecutionListener().executionFinished(tt, TestExecutionResult.successful());
            });


            request.getEngineExecutionListener().executionFinished(testMethod, TestExecutionResult.successful());
        });
    }
}
