package org.chy.anubis.testengine.junit.descriptor;

import org.junit.platform.engine.TestDescriptor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TrialRootTestDescriptor extends AbstractTestDescriptor<AlgorithmTestDescriptor> {

    String groupName;

    public TrialRootTestDescriptor(String groupName) {
        super(groupName);
        this.groupName = groupName;
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return true;
    }
}
