package org.chy.anubis.dynamic.classloader;

import org.chy.anubis.dynamic.compiler.AnubisCompilerContext;

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
        return defineClass(name, classData, 0, classData.length);
    }
}
