package org.chy.anubis.dynamic;

import lombok.Getter;
import lombok.SneakyThrows;
import org.chy.anubis.dynamic.classloader.AnubisClassLoader;
import org.chy.anubis.dynamic.compiler.AnubisCompilerContext;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.utils.ListUtils;

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

    /**
     * 加载对应的class对象, 如果不存在, 则使用传入的 JavaFile 对象去编译加载
     * @param javaFile
     */
    @SneakyThrows
    public Class<?> loadClass(JavaFile javaFile){
        String javaAllClassName = javaFile.getJavaAllClassName();
        //还没有对应的 class对象,那么开始编译
        if(!compilerContext.isExistClass(javaAllClassName)){
            compiler(ListUtils.to(javaFile));
        }
        return classLoader.loadClass(javaAllClassName);
    }
}
