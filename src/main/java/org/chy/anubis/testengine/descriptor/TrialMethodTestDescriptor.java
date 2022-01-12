package org.chy.anubis.testengine.descriptor;


import org.chy.anubis.annotation.Trial;
import org.junit.platform.engine.TestDescriptor;

import java.lang.reflect.Method;
import java.util.Optional;

public class TrialMethodTestDescriptor extends AbstractTestDescriptor {

    final Method targetMethod;
    final Trial trial;


    private TrialMethodTestDescriptor(Method targetMethod, Trial trial) {
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

    /**
     * 如果是合适的方法,那么就构造对应的 TrialMethodTestDescriptor 对象
     *
     * @param method
     * @return
     */
    public static Optional<TrialMethodTestDescriptor> checkAndBuild(Method method) {
        Trial trialAnnotation = method.getAnnotation(Trial.class);
        if (trialAnnotation == null) {
            return Optional.empty();
        }
        return Optional.of(new TrialMethodTestDescriptor(method, trialAnnotation));
    }


}
