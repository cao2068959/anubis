package org.chy.anubis.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.chy.anubis.exception.JavaParserException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 整个 java源文件的解析器
 */
public class JavaSourceParser {

    CompilationUnit root;


    String packagePath = null;
    String className = null;

    TypeDeclaration<?> typeDeclaration;

    List<MethodDeclaration> methods;

    public JavaSourceParser(CompilationUnit parseData) {
        this.root = parseData;
    }

    public String getClassName() {
        if (className == null) {
            TypeDeclaration<?> type = getType();
            className = type.getNameAsString();
        }
        return className;
    }

    public String getPackagePath() {
        if (packagePath == null) {
            packagePath = root.getPackageDeclaration().orElseThrow(() -> new JavaParserException("解析 java文件失败")).getNameAsString();
        }
        return packagePath;
    }

    public List<MethodDeclaration> getMethod() {
        if (methods == null) {
            TypeDeclaration<?> type = getType();
            this.methods = type.getMethods();
        }
        return methods;
    }

    public List<String> getImports() {
        return root.getImports().stream().map(ImportDeclaration::toString).collect(Collectors.toList());
    }

    public Optional<MethodDeclaration> getFirstMethod(){
        List<MethodDeclaration> method = getMethod();
        return method.stream().findFirst();
    }


    public TypeDeclaration<?> getType() {
        if (typeDeclaration == null) {
            this.typeDeclaration = root.getTypes().getFirst().orElseThrow(() -> new JavaParserException("解析 java文件失败"));
        }
        return typeDeclaration;
    }


}
