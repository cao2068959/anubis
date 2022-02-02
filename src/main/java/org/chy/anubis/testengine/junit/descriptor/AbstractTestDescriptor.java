package org.chy.anubis.testengine.junit.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;

import java.util.*;

public abstract class AbstractTestDescriptor<T extends TestDescriptor> implements TestDescriptor {

    String name;
    TestDescriptor parent;
    Set<T> children = new HashSet<>();


    public AbstractTestDescriptor(String name) {
        this.name = name;
    }

    @Override
    public UniqueId getUniqueId() {
        return UniqueId.forEngine(name);
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public Set<TestTag> getTags() {
        return new HashSet<>();
    }

    @Override
    public Optional<TestSource> getSource() {
        return Optional.empty();
    }


    @Override
    public Set<? extends TestDescriptor> getChildren() {
        return children;
    }

    @Override
    public void addChild(TestDescriptor descriptor) {
        descriptor.setParent(this);
        children.add((T) descriptor);
    }

    @Override
    public void removeChild(TestDescriptor descriptor) {
        descriptor.setParent(null);
        children.remove(descriptor);
    }

    @Override
    public void removeFromHierarchy() {

    }

    @Override
    public Optional<TestDescriptor> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void setParent(TestDescriptor parent) {
        this.parent = parent;
    }

    @Override
    public Optional<? extends TestDescriptor> findByUniqueId(UniqueId uniqueId) {
        return Optional.empty();
    }


}
