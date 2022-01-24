package org.chy.anubis.testengine.junit.descriptor;


import org.chy.anubis.annotation.Trial;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TrialMethodTestDescriptorBuilder {

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

    /**
     * 使用 class构造出 TrialMethodTestDescriptor
     * 这里会去把这个class中打了注解 @Trial 的方法给包装成 TrialMethodTestDescriptor
     *
     * @param targetClass
     * @return
     */
    public static Set<TrialMethodTestDescriptor> build(Class targetClass) {
        Set<TrialMethodTestDescriptor> result = new HashSet<>();
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            checkAndBuild(method).ifPresent(result::add);
        }
        return result;
    }

}
