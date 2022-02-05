package org.chy.anubis.testengine.junit;


import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.exception.AlgorithmCaseCollectException;

import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.TrialRootTestDescriptor;
import org.chy.anubis.testengine.junit.executioner.AlgorithmTestExecutioner;
import org.chy.anubis.testengine.junit.executioner.CommonExecutioner;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;


import java.util.*;
import java.util.stream.Collectors;

public class TrialTestEngine implements TestEngine {


    @Override
    public String getId() {
        return "trialTestEngine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        TrialRootTestDescriptor result = new TrialRootTestDescriptor("anubis-trial-root");

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
        algorithmMethodDefinitions.stream().map(this::buildAlgorithmDescriptor).forEach(result::addChild);
        return result;
    }

    @Override
    public void execute(ExecutionRequest request) {
        TestDescriptor testDescriptor = request.getRootTestDescriptor();
        if (!(testDescriptor instanceof TrialRootTestDescriptor)) {
            return;
        }
        EngineExecutionListener listener = request.getEngineExecutionListener();
        TrialRootTestDescriptor rootTestDescriptor = (TrialRootTestDescriptor) testDescriptor;

        //执行root的用例
        CommonExecutioner<TrialRootTestDescriptor> rootExecutioner = new CommonExecutioner<>(listener, rootTestDescriptor);
        rootExecutioner.run(root -> {
            //遍历有多少个算法
            root.foreachChild(algorithmTest -> {
                AlgorithmTestExecutioner algorithmTestExecutioner = new AlgorithmTestExecutioner(listener, algorithmTest);
                //开始执行这个算法的测试
                algorithmTestExecutioner.start();
            });
        });

    }

    /**
     * 去构建一个 AlgorithmDescriptor 出来, "算法"里面真正去执行的是"用例" 所以在构建"算法"的时候也会把"用例"给一起构建了
     *
     * @param algorithmMethodDefinition
     * @return
     */
    private AlgorithmTestDescriptor buildAlgorithmDescriptor(AlgorithmMethodDefinition algorithmMethodDefinition) {
        String algorithmName = algorithmMethodDefinition.getAlgorithmName();
        AlgorithmTestDescriptor result = new AlgorithmTestDescriptor(algorithmName);
        //去获取这个算法将要运行的测试用例,并把他转成对应的descriptor
        findCaseList(algorithmMethodDefinition).stream()
                .map(caseBriefInfo -> new CaseTestDescriptor(algorithmName + "_" + caseBriefInfo.getName(), caseBriefInfo))
                .forEach(result::addChild);
        result.setAlgorithmMethodDefinition(algorithmMethodDefinition);
        return result;
    }


    /**
     * 根据 algorithmMethodDefinition 去获取这个算法下面对应的测试用例列表
     *
     * @param definition
     * @return
     */
    private List<CaseBriefInfo> findCaseList(AlgorithmMethodDefinition definition) {
        List<CaseBriefInfo> result = new ArrayList<>();
        //如果没有指定,那么从远程仓库中获取对应的测试用例列表
        List<CaseBriefInfo> caseCatalogs = WarehouseHolder.warehouse.getCaseCatalog(definition.getCaseSourceType(),
                definition.getAlgorithmName());

        Set<String> excludeCaseNames = definition.getExcludeCaseName();

        //指定了强制要运行的案例,那么就返回指定的案列,这么还需要去看看 是否在排除用例里面
        if (definition.isAppointRun()) {
            Map<String, CaseBriefInfo> mapping =
                    caseCatalogs.stream().collect(Collectors.toMap(CaseBriefInfo::getName, caseBriefInfo -> caseBriefInfo));
            Set<String> runCaseNames = definition.getRunCaseName();
            for (String runCaseName : runCaseNames) {
                if (excludeCaseNames.contains(runCaseName)) {
                    continue;
                }
                CaseBriefInfo caseBriefInfo = mapping.get(runCaseName);
                if (caseBriefInfo == null) {
                    throw new AlgorithmCaseCollectException("算法[" + definition.getAlgorithmName() + "] 指定运行的测试用例[" + runCaseName + "] 不存在");
                }
                result.add(caseBriefInfo);
            }
            return result;
        }

        //如果没有手动去指定要运行的案例,那么按照分页取对应的条数
        int limit = definition.getLimit();
        int startIndex = definition.getStartIndex();
        int index = 0;
        for (CaseBriefInfo caseBriefInfo : caseCatalogs) {
            if (startIndex > index) {
                index++;
                continue;
            }
            //取到对应的条数了
            if (result.size() >= limit) {
                break;
            }

            //被排除了找一下个
            if (excludeCaseNames.contains(caseBriefInfo.getName())) {
                continue;
            }
            result.add(caseBriefInfo);
        }
        return result;
    }


}
