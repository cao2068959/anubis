package org.chy.anubis.testengine.junit;


import org.chy.anubis.enums.CaseSourceType;
import org.chy.anubis.enums.TreasuryType;
import org.chy.anubis.property.PropertyContext;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.property.mapping.AnubisProperty;
import org.chy.anubis.testengine.junit.descriptor.TrialMethodTestDescriptorBuilder;
import org.chy.anubis.testengine.junit.descriptor.TrialRootTestDescriptor;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;


import java.util.Optional;
import java.util.Set;

public class TrialTestEngine implements TestEngine {


    @Override
    public String getId() {
        return "trialTestEngine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        PropertyContext context = PropertyContextHolder.context;
        TrialRootTestDescriptor result = new TrialRootTestDescriptor("TrialRootTestDescriptor");
        //通过class去测试的，扫描这个class下面的所有方法
        discoveryRequest.getSelectorsByType(ClassSelector.class).stream().map(ClassSelector::getJavaClass)
                .map(TrialMethodTestDescriptorBuilder::build).flatMap(Set::stream).forEach(result::addChild);

        //如果没有拿到class说明可能是直接去运行的方法
        discoveryRequest.getSelectorsByType(MethodSelector.class).stream().map(MethodSelector::getJavaMethod)
                .map(TrialMethodTestDescriptorBuilder::checkAndBuild).filter(Optional::isPresent).map(Optional::get)
                .forEach(result::addChild);

        AnubisProperty anubis = PropertyContextHolder.getAnubisProperty();
        String host = anubis.treasury.anubisService.host;
        TreasuryType type = anubis.treasury.type;

        WarehouseHolder.warehouse.getCaseCatalog(CaseSourceType.LEETCODE, "xxxx");


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return result;
    }

    @Override
    public void execute(ExecutionRequest request) {
        TestDescriptor testDescriptor = request.getRootTestDescriptor();
        if (!(testDescriptor instanceof TrialRootTestDescriptor)) {
            return;
        }
        TrialRootTestDescriptor rootTestDescriptor = (TrialRootTestDescriptor) testDescriptor;
        rootTestDescriptor.foreach(trialMethodTestDescriptor -> {
            request.getEngineExecutionListener().executionStarted(trialMethodTestDescriptor);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            request.getEngineExecutionListener().executionFinished(trialMethodTestDescriptor, TestExecutionResult.successful());


        });

    }
}
