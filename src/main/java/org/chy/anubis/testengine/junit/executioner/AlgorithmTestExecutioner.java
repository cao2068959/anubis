package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.log.Logger;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.WarehouseUtils;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.chy.anubis.Constant.ALGORITHM_INTERFACE_NAME;

public class AlgorithmTestExecutioner extends CommonExecutioner<AlgorithmTestDescriptor> {


    private final AlgorithmMethodDefinition definition;

    public AlgorithmTestExecutioner(EngineExecutionListener listener, AlgorithmTestDescriptor testDescriptor) {
        super(listener, testDescriptor);
        this.definition = testDescriptor.getAlgorithmMethodDefinition();
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

        findAlgorithmInterface();
        String allCaseName = testChildren.stream().map(CaseTestDescriptor::getDisplayName).collect(Collectors.joining(" , "));
        Logger.info("将要执行的测试用例为: [" + allCaseName + "]");


        //遍历这个算法下面所有的测试案例
        algorithmTestDescriptor.foreachChild(caseTestDescriptor -> {
            CaseTestExecutioner executioner = new CaseTestExecutioner(listener, caseTestDescriptor);
            //开始执行这个测试用例
            executioner.start();
        });
    }

    /**
     * 获取这个算法在远程仓库端定义的算法接口
     */
    private Optional<FileInfo> findAlgorithmInterface() {
        Optional<FileInfo> fileInfo = WarehouseHolder.warehouse.getFileInfo(WarehouseUtils
                .getPathFromAlgorithmPath(definition.getCaseSourceType(), definition.getAlgorithmName(),
                        ALGORITHM_INTERFACE_NAME));


        return fileInfo;

    }


}
