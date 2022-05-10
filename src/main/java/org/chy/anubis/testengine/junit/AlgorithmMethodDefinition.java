package org.chy.anubis.testengine.junit;

import lombok.Getter;
import org.chy.anubis.annotation.Trial;
import org.chy.anubis.enums.CaseSourceType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class AlgorithmMethodDefinition {


    Method method;
    String algorithmName;
    CaseSourceType caseSourceType;
    int limit;
    int startIndex;
    Set<String> excludeCaseName;
    Set<String> runCaseName;


    private AlgorithmMethodDefinition(Method method, Trial trial) {
        this.method = method;
        parseAnnotation(trial);
    }

    private void parseAnnotation(Trial trial) {
        this.caseSourceType = trial.caseSourceType();
        this.algorithmName = trial.value();
        if (algorithmName.contains("-")) {
            algorithmName = algorithmName.replace("-", "_");
        }
        this.limit = trial.limit();
        this.startIndex = trial.startIndex();
        this.excludeCaseName = Arrays.stream(trial.excludeCaseName()).collect(Collectors.toSet());
        this.runCaseName = Arrays.stream(trial.runCaseName()).collect(Collectors.toSet());
    }

    public static Optional<AlgorithmMethodDefinition> checkAndBuild(Method method) {
        Trial trialAnnotation = method.getAnnotation(Trial.class);
        if (trialAnnotation == null) {
            return Optional.empty();
        }
        return Optional.of(new AlgorithmMethodDefinition(method, trialAnnotation));
    }


    public static Set<AlgorithmMethodDefinition> build(Class targetClass) {
        Set<AlgorithmMethodDefinition> result = new HashSet<>();
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            checkAndBuild(method).ifPresent(result::add);
        }
        return result;
    }


    public boolean isAppointRun() {
        return !runCaseName.isEmpty();
    }

    /**
     * 根据查询到用例名称和配置信息来获取应该返回的用例信息
     */
    public Set<String> findCaseName() {
        if (!runCaseName.isEmpty()) {
            return new HashSet<>(runCaseName);
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmMethodDefinition that = (AlgorithmMethodDefinition) o;
        return algorithmName.equals(that.algorithmName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(algorithmName);
    }

}
