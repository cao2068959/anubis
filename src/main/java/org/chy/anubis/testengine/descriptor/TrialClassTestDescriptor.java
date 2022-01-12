package org.chy.anubis.testengine.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class TrialClassTestDescriptor extends AbstractTestDescriptor {

    String groupName;
    Optional<Class> targetClass = Optional.empty();
    Set<TrialMethodTestDescriptor> allTest = new HashSet<>();

    public TrialClassTestDescriptor(String groupName) {
        super(groupName);
        this.groupName = groupName;
    }

    public TrialClassTestDescriptor(Class javaClass) {
        super(javaClass.getTypeName());
        this.targetClass = Optional.of(javaClass);
        scanClass(javaClass);
    }

    /**
     * 扫描整个class,找到所有需要处理的方法
     */
    private void scanClass(Class javaClass) {
        //扫描这个类下面所有的方法寻找有没有打了注解 @Trial 的
        Method[] methods = javaClass.getDeclaredMethods();
        for (Method method : methods) {
            TrialMethodTestDescriptor.checkAndBuild(method).ifPresent(this::addChild);
        }
    }

    public void foreach(Consumer<TrialMethodTestDescriptor> consumer) {
        for (TrialMethodTestDescriptor trialMethodTestDescriptor : allTest) {
            consumer.accept(trialMethodTestDescriptor);
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
        allTest.add((TrialMethodTestDescriptor) descriptor);
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
        throw new TestDescriptorNonSupportException("TrialTestGroupDescriptor 不支持类型: ["
                + descriptor.getClass().toString() + "]");
    }


}
