package org.chy.anubis.dynamic.classloader;

import org.chy.anubis.dynamic.compiler.AnubisCompilerContext;
import org.chy.anubis.inject.InjectManager;

public class AnubisClassLoader extends ClassLoader {

    AnubisCompilerContext anubisCompilerContext;

    public AnubisClassLoader(ClassLoader parent, AnubisCompilerContext compilerContext) {
        super(parent);
        anubisCompilerContext = compilerContext;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = anubisCompilerContext.findClass(name);
        if (classData == null) {
            return super.findClass(name);
        }
        Class<?> result = defineClass(name, classData, 0, classData.length);
        //如果有对象需要注入的那么处理一下
        InjectManager.inject(result);
        return result;
    }
}
