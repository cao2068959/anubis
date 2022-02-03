package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.junit.platform.engine.EngineExecutionListener;


public class CaseTestExecutioner extends CommonExecutioner<CaseTestDescriptor> {

    public CaseTestExecutioner(EngineExecutionListener listener, CaseTestDescriptor testDescriptor) {
        super(listener, testDescriptor);
    }


    public void start() {
        run(this::doStart);
    }

    private void doStart(CaseTestDescriptor caseTestDescriptor) {

    }

}
