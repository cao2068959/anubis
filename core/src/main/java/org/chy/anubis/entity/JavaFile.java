package org.chy.anubis.entity;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.chy.anubis.exception.JavaParserException;
import org.chy.anubis.javaparser.JavaParserContainer;
import org.chy.anubis.javaparser.JavaSourceParser;

import java.util.List;

public class JavaFile extends FileInfo {

    /**
     * java的全路径如: org.chy.anubis.entity.FileInfo
     */
    private String javaAllClassName;

    /**
     * java中的类名如 JavaFile 没有后缀
     */
    private String javaClassName;

    private String packagePath;

    /**
     * 整个java文件解析后放这里面
     */
    private JavaSourceParser javaSourceParser;

    @Override
    public void setBlobData(String blobData) {
        super.setBlobData(blobData);
    }

    /**
     * 为了性能当使用到的时候再去解析java文件
     *
     * @return
     */
    private JavaSourceParser getJavaSourceParser() {
        if (javaSourceParser != null) {
            return javaSourceParser;
        }
        if (blobData == null) {
            throw new JavaParserException("文件 [" + getName() + "] 没有传入数据内容,不能解析对应的文件结构");
        }
        javaSourceParser = JavaParserContainer.parse(blobData);
        return javaSourceParser;
    }


    public String getJavaClassName() {
        if (javaClassName == null) {
            javaClassName = getJavaSourceParser().getClassName();
        }
        return javaClassName;
    }

    public String getPackagePath() {
        if (packagePath == null) {
            packagePath = getJavaSourceParser().getPackagePath();
        }
        return packagePath;
    }

    public String getJavaAllClassName() {
        if (javaAllClassName == null) {
            javaAllClassName = getPackagePath() + "." + getJavaClassName();
        }
        return javaAllClassName;
    }

    public List<String> getImports() {
        return  getJavaSourceParser().getImports();
    }
    /**
     * 获取这个类中的第一个方法
     */
    public JavaMethodInfo getFirstMethod() {
        MethodDeclaration methodDeclaration = getJavaSourceParser().getFirstMethod()
                .orElseThrow(() -> new JavaParserException("[" + getJavaAllClassName() + "] 中没有任何的方法"));
        return new JavaMethodInfo(methodDeclaration);
    }

}
