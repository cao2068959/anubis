package org.chy.anubis.entity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;
import lombok.Getter;
import org.chy.anubis.utils.StringUtils;

import java.util.Optional;

public class JavaMethodInfo {

    MethodDeclaration methodDeclaration;

    @Getter
    String methodSignature;


    public JavaMethodInfo(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
        genMethodSignature();
    }

    private void genMethodSignature() {
        StringBuilder result = new StringBuilder();
        methodDeclaration.getModifiers().forEach(modifier -> {
            result.append(modifier).append(" ");
        });
        result.append(methodDeclaration.getType());
        result.append(methodDeclaration.getName());

        String parameters = StringUtils.join("(", ")", methodDeclaration.getParameters(), Node::toString);
        result.append(parameters);
        this.methodSignature = result.toString();
    }

    public Optional<String> getReturnType() {
        Type type = methodDeclaration.getType();
        return Optional.ofNullable(type.asString());
    }

}
