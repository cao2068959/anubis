package org.chy.anubis.testengine.junit.descriptor;

import org.junit.platform.engine.TestDescriptor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TrialRootTestDescriptor extends AbstractTestDescriptor {

    String groupName;
    Map<String, TrialMethodTestDescriptor> allTest = new HashMap();

    public TrialRootTestDescriptor(String groupName) {
        super(groupName);
        this.groupName = groupName;
    }

    public void foreach(Consumer<TrialMethodTestDescriptor> consumer) {
        allTest.forEach((key, value) -> consumer.accept(value));
    }

    @Override
    public Set<? extends TestDescriptor> getChildren() {
        return new HashSet<>(allTest.values());
    }

    @Override
    public void addChild(TestDescriptor descriptor) {
        checkTestDescriptorType(descriptor);
        allTest.put(descriptor.getDisplayName(), (TrialMethodTestDescriptor) descriptor);
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
        if (descriptor instanceof TrialMethodTestDescriptor) {
            return;
        }
        throw new TestDescriptorNonSupportException("TrialRootTestDescriptor 不支持类型: ["
                + descriptor.getClass().toString() + "]");
    }


}
