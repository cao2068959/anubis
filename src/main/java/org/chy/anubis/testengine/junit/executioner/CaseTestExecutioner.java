package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.junit.platform.engine.EngineExecutionListener;


public class CaseTestExecutioner extends CommonExecutioner<CaseTestDescriptor> {

    JavaFile algorithmInterface;

    public CaseTestExecutioner(EngineExecutionListener listener, CaseTestDescriptor testDescriptor, JavaFile algorithmInterface) {
        super(listener, testDescriptor);
        this.algorithmInterface = algorithmInterface;
    }


    public void start() {
        run(this::doStart);
    }

    private void doStart(CaseTestDescriptor caseTestDescriptor) {

    }

}
