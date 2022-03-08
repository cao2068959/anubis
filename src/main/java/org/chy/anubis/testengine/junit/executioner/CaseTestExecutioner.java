package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.dynamic.DynamicRunEngine;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.WarehouseUtils;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.EngineExecutionListener;

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

        System.out.println(templateCase);



    }

}
