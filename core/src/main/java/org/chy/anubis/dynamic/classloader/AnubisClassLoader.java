package org.chy.anubis.dynamic.classloader;

import org.chy.anubis.Constant;
import org.chy.anubis.dynamic.compiler.AnubisCompilerContext;
import org.chy.anubis.inject.InjectManager;

public class AnubisClassLoader extends ClassLoader {

    AnubisCompilerContext anubisCompilerContext;

    public AnubisClassLoader(ClassLoader parent, AnubisCompilerContext compilerContext) {
        super(parent);
        anubisCompilerContext = compilerContext;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (isLoadClass(name)) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> result = findLoadedClass(name);
                if (result != null) {
                    return result;
                }
                result = findClass(name);
                //第一次加载的话 注入一些静态属性
                InjectManager.inject(result);
                return result;
            }
        }
        Class<?> aClass = super.loadClass(name, resolve);
        InjectManager.inject(aClass);
        return aClass;
    }

    private boolean isLoadClass(String name) {
        if (!name.startsWith(Constant.TREASURY_BASE_PATH)) {
            return false;
        }
        if (name.startsWith(Constant.TREASURY_BASE_PATH + ".annotations")) {
            return false;
        }

        if (name.startsWith(Constant.TREASURY_BASE_PATH + ".log")) {
            return false;
        }
        return true;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = anubisCompilerContext.findClass(name);
        if (classData == null) {
            return super.findClass(name);
        }
        return defineClass(name, classData, 0, classData.length);
    }
}
