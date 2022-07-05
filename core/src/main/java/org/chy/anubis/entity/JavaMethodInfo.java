package org.chy.anubis.entity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;
import lombok.Getter;
import org.chy.anubis.utils.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaMethodInfo {

    MethodDeclaration methodDeclaration;

    /**
     * 不包含 Modifiers
     */
    @Getter
    String methodSignature;


    public JavaMethodInfo(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
        genMethodSignature();
    }

    private void genMethodSignature() {
        StringBuilder result = new StringBuilder();
        result.append(methodDeclaration.getType()).append(" ");
        result.append(methodDeclaration.getName());
        String parameters = StringUtils.join("(", ")", methodDeclaration.getParameters(), Node::toString);
        result.append(parameters);
        this.methodSignature = result.toString();
    }


    public Optional<String> getReturnType() {
        Type type = methodDeclaration.getType();
        return Optional.ofNullable(type.asString());
    }

    public List<ParameterInfo> getParameter() {
        return methodDeclaration.getParameters().stream().map(parameter -> {
            ParameterInfo parameterInfo = new ParameterInfo(parameter.getTypeAsString(),
                    parameter.getNameAsString());
            Set<String> annotations = parameter.getAnnotations().stream()
                    .map(annotationExpr -> annotationExpr.getName().asString()).collect(Collectors.toSet());
            parameterInfo.setAnnotations(annotations);
            return parameterInfo;
        }).collect(Collectors.toList());
    }

}
