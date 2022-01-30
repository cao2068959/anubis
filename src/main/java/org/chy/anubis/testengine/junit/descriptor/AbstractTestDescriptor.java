package org.chy.anubis.testengine.junit.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractTestDescriptor implements TestDescriptor {

    String name;

    TestDescriptor parent;

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
        return new HashSet<>();
    }

    @Override
    public void addChild(TestDescriptor descriptor) {

    }

    @Override
    public void removeChild(TestDescriptor descriptor) {

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
