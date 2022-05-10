package org.chy.anubis.testengine.junit.descriptor;


import lombok.Getter;
import lombok.Setter;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;

public class AlgorithmTestDescriptor extends AbstractTestDescriptor<CaseTestDescriptor> {

    @Getter
    @Setter
    AlgorithmMethodDefinition algorithmMethodDefinition;


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
