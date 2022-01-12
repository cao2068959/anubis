package org.chy.anubis.testengine.descriptor;

import org.junit.platform.engine.TestDescriptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class TrialRootTestDescriptor extends AbstractTestDescriptor {

    String groupName;
    Optional<Class> targetClass = Optional.empty();
    Set<TrialClassTestDescriptor> allTest = new HashSet<>();

    public TrialRootTestDescriptor(String groupName) {
        super(groupName);
        this.groupName = groupName;
    }

    public void foreach(Consumer<TrialClassTestDescriptor> consumer) {
        for (TrialClassTestDescriptor trialClassTestDescriptor : allTest) {
            consumer.accept(trialClassTestDescriptor);
        }
    }


    @Override
    public Set<? extends TestDescriptor> getChildren() {
        return allTest;
    }

    @Override
    public void addChild(TestDescriptor descriptor) {
        checkTestDescriptorType(descriptor);
        descriptor.setParent(this);
        allTest.add((TrialClassTestDescriptor) descriptor);
    }

    @Override
    public Type getType() {
        return Type.CONTAINER_AND_TEST;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    private void checkTestDescriptorType(TestDescriptor descriptor) {
        if (descriptor instanceof TrialClassTestDescriptor) {
            return;
        }
        throw new TestDescriptorNonSupportException("TrialRootTestDescriptor 不支持类型: ["
                + descriptor.getClass().toString() + "]");
    }


}
