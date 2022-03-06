package org.chy.anubis.javaparser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import org.chy.anubis.exception.JavaParserException;

/**
 * 整个 java源文件的解析器
 */
public class JavaSourceParser {

    CompilationUnit root;


    String packagePath = null;
    String className = null;


    public JavaSourceParser(CompilationUnit parseData) {
        this.root = parseData;
    }

    public String getClassName() {
        if (className == null) {
            className = root.getTypes().getFirst().map(NodeWithSimpleName::getNameAsString).orElseThrow(() -> new JavaParserException("解析 java文件失败"));
        }
        return className;
    }

    public String getPackagePath() {
        if (packagePath == null) {
            packagePath = root.getPackageDeclaration().orElseThrow(() -> new JavaParserException("解析 java文件失败")).getNameAsString();
        }
        return packagePath;
    }


}
