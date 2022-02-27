package org.chy.anubis.compiler;

import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import org.chy.anubis.entity.ClassFile;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.exception.CompilerException;
import org.chy.anubis.utils.StringUtils;

import javax.tools.StandardJavaFileManager;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 整个anubis项目的动态编译管理器, 使用 compiler() 方法可以动态编译, 将会编译输入文件以及会自动获取依赖文件一起进行编译
 * <p>
 * 使用方法 findClass() 可以获取已经编译好的class文件
 */
public class AnubisCompilerContext {

    private static final JavacTool javaCompiler;
    private static final StandardJavaFileManager standardFileManager;
    private AnubisJavaFileManager anubisJavaFileManager;

    static {
        javaCompiler = JavacTool.create();
        standardFileManager = javaCompiler.getStandardFileManager(diagnostic -> {
                },
                Locale.CHINESE, StandardCharsets.UTF_8);
    }

    public AnubisCompilerContext() {
        anubisJavaFileManager = new AnubisJavaFileManager(standardFileManager);
    }

    public void compiler(List<JavaFile> javaFiles) {
        List<JavaSourceFileObject> javaSourceFileObjects = javaFiles.stream().map(JavaSourceFileObject::new)
                .collect(Collectors.toList());
        JavacTask task = javaCompiler.getTask(null, anubisJavaFileManager, null, null, null,
                javaSourceFileObjects);

        Boolean result = task.call();
        if (!result) {
            String fileMsg = StringUtils.join("[", "]", javaFiles, JavaFile::getName);
            throw new CompilerException("动态编译文件失败 " + fileMsg);
        }
    }

    /**
     * 获取编译好的 class文件, 虽然只是 compiler(a.java) 其中 a.java依赖了 c.java 那么同样 findClass可以获取 c.class
     * 该方法同样可以获取 java.lang.String, 但不建议这么做
     *
     * @param path
     * @return
     */
    public ClassFile findClass(String path) {
        return null;
    }
}
