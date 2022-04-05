package org.chy.anubis.testengine.junit.executioner;

import org.chy.anubis.testengine.junit.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;

import java.util.function.Consumer;

public class CommonExecutioner<T extends AbstractTestDescriptor> {

    protected EngineExecutionListener listener;
    protected T testDescriptor;

    public CommonExecutioner(EngineExecutionListener listener, T testDescriptor) {
        this.testDescriptor = testDescriptor;
        this.listener = listener;
    }

    public void run(Consumer<T> consumer) {
        listener.executionStarted(testDescriptor);
        try {
            consumer.accept(testDescriptor);
            listener.executionFinished(testDescriptor, TestExecutionResult.successful());
        } catch (Exception e) {
            listener.executionFinished(testDescriptor, TestExecutionResult.failed(e));
        }
    }




}
