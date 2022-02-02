package org.chy.anubis.testengine.junit.descriptor;


public class AlgorithmTestDescriptor extends AbstractTestDescriptor {

    public AlgorithmTestDescriptor(String name) {
        super(name);
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

}
