package org.chy.anubis.testengine.junit.descriptor;


import org.chy.anubis.annotation.Trial;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class TrialMethodTestDescriptor extends AbstractTestDescriptor {

    final Method targetMethod;
    final Trial trial;


    public TrialMethodTestDescriptor(Method targetMethod, Trial trial) {
        super(targetMethod.getName());
        this.targetMethod = targetMethod;
        this.trial = trial;
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
