package org.chy.anubis.dynamic;

import lombok.Getter;
import lombok.SneakyThrows;
import org.chy.anubis.dynamic.classloader.AnubisClassLoader;
import org.chy.anubis.dynamic.compiler.AnubisCompilerContext;
import org.chy.anubis.entity.JavaFile;

import java.util.List;

/**
 * 动态运行引擎, 编译类&加载 一体化
 */
public class DynamicRunEngine {

    /**
     * 编译器
     */
    private final AnubisCompilerContext compilerContext;

    /**
     * 类加载器
     */
    @Getter
    private final AnubisClassLoader classLoader;

    public DynamicRunEngine() {
        compilerContext = new AnubisCompilerContext();
        classLoader = new AnubisClassLoader(Thread.currentThread().getContextClassLoader(),compilerContext);
    }

    /**
     * 编译对应的java文件
     *
     * @param javaFile
     */
    public void compiler(List<JavaFile> javaFile) {
        compilerContext.compiler(javaFile);
    }

    public void getJavaSource(String allPath) {


    }

    @SneakyThrows
    public Class<?> loadClass(String javaAllClassName) {
        return classLoader.loadClass(javaAllClassName);
    }
}
