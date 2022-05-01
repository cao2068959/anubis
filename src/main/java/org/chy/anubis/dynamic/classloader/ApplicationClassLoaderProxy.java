package org.chy.anubis.dynamic.classloader;

import org.chy.anubis.utils.ClassLoaderUtils;

import java.net.URL;
import java.net.URLClassLoader;

public class ApplicationClassLoaderProxy extends URLClassLoader {


    private final ClassLoader appClassLoader;

    public ApplicationClassLoaderProxy(ClassLoader appClassLoader) {
        super(getSystemUrl(), appClassLoader.getParent());
        this.appClassLoader = appClassLoader;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ("org.chy.anubis.dynamic.template.TestCaseExecuter".equals(name)){
            return appClassLoader.loadClass(name);
        }

        if (name.startsWith("org.chy.anubis")) {
            return super.loadClass(name, resolve);
        }
        return appClassLoader.loadClass(name);
    }


    private static URL[] getSystemUrl() {
        URL[] urls = ClassLoaderUtils.getSystemClassLoaderUrl().toArray(new URL[0]);
        return urls;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
