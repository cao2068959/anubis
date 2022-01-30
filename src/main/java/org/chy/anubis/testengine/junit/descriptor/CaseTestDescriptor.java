package org.chy.anubis.testengine.junit.descriptor;

public class CaseTestDescriptor extends AbstractTestDescriptor {

    public CaseTestDescriptor(String name) {
        super(name);
    }


    @Override
    public Type getType() {
        return Type.CONTAINER_AND_TEST;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isTest() {
        return true;
    }
}
