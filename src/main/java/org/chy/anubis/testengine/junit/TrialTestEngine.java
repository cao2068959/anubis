package org.chy.anubis.testengine.junit;


import org.chy.anubis.enums.TreasuryType;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.property.mapping.AnubisProperty;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmDescriptor;
import org.chy.anubis.testengine.junit.descriptor.TrialRootTestDescriptor;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TrialTestEngine implements TestEngine {


    @Override
    public String getId() {
        return "trialTestEngine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        //先收集所有打了@Trial 注解的算法方法
        Set<AlgorithmMethodDefinition> algorithmMethodDefinitions = new HashSet<>();

        //通过class去测试的，扫描这个class下面的所有方法
        discoveryRequest.getSelectorsByType(ClassSelector.class).stream().map(ClassSelector::getJavaClass)
                .map(AlgorithmMethodDefinition::build).flatMap(Set::stream).forEach(algorithmMethodDefinitions::add);

        //如果没有拿到class说明可能是直接去运行的方法
        discoveryRequest.getSelectorsByType(MethodSelector.class).stream().map(MethodSelector::getJavaMethod)
                .map(AlgorithmMethodDefinition::checkAndBuild).filter(Optional::isPresent).map(Optional::get)
                .forEach(algorithmMethodDefinitions::add);

        //每一个算法方法都去远程调用去查询这个算法下面具体的测试用例是什么
        algorithmMethodDefinitions.forEach(algorithmMethodDefinition -> {
            WarehouseHolder.warehouse.getCaseCatalog(algorithmMethodDefinition.getCaseSourceType(),
                    algorithmMethodDefinition.getAlgorithmName());
        });




        AnubisProperty anubis = PropertyContextHolder.getAnubisProperty();
        String host = anubis.treasury.anubisService.host;
        TreasuryType type = anubis.treasury.type;




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

            request.getEngineExecutionListener().executionFinished(trialMethodTestDescriptor, TestExecutionResult.successful());


        });

    }

    /**
     * 去构建一个 AlgorithmDescriptor 出来, "算法"里面真正去执行的是"用例" 所以在构建"算法"的时候也会把"用例"给一起构建了
     *
     * @param algorithmMethodDefinition
     * @return
     */
    public AlgorithmDescriptor buildAlgorithmDescriptor(AlgorithmMethodDefinition algorithmMethodDefinition){



    }


}
