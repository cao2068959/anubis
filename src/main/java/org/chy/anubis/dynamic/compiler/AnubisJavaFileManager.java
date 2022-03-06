package org.chy.anubis.dynamic.compiler;


import org.chy.anubis.entity.Pair;
import org.chy.anubis.utils.StringUtils;

import javax.tools.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.chy.anubis.Constant.TREASURY_BASE_PATH;

public class AnubisJavaFileManager implements JavaFileManager {

    StandardJavaFileManager standardJavaFileManager;

    //key:对应的包路径 value:对应这个路径下的所有class文件
    Map<String, List<JavaFileObject>> classCache = new ConcurrentHashMap<>();

    public AnubisJavaFileManager(StandardJavaFileManager standardJavaFileManager) {
        this.standardJavaFileManager = standardJavaFileManager;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return null;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        //查找编译所依赖的 class文件
        if (location == StandardLocation.CLASS_PATH) {
            return findClassJavaFileObject(location, packageName, kinds, recurse);
        }
        //jdk下那些依赖的包
        if (location == StandardLocation.PLATFORM_CLASS_PATH) {
            return standardJavaFileManager.list(location, packageName, kinds, recurse);
        }

        return new ArrayList<>();

    }

    /**
     * 查找 对应包路径下面的 class文件, 为了提高效率这里将会把 org.chy.anubis.treasury 包下面的排掉
     *
     * @param location
     * @param packageName
     * @param kinds
     * @param recurse
     * @return
     * @throws IOException
     */
    private List<JavaFileObject> findClassJavaFileObject(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        //先从缓存中查找
        if (StandardLocation.CLASS_PATH == location) {
            List<JavaFileObject> result = classCache.get(packageName);
            if (result != null) {
                return result;
            }
        }

        //查找的路径是 anubis-treasury 项目下面的,如果缓存没找到,说明没开始编译就不再去下面查找了
        if (packageName.startsWith(TREASURY_BASE_PATH)) {
            List<JavaFileObject> result = new ArrayList<>();
            classCache.put(packageName, result);
            return result;
        }

        //去系统路径查找
        Iterable<JavaFileObject> localClass = standardJavaFileManager.list(location, packageName, kinds, recurse);
        List<JavaFileObject> result = new ArrayList<>();
        for (JavaFileObject javaFileObject : localClass) {
            result.add(javaFileObject);
        }
        return result;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof JavaClassFileObject) {
            JavaClassFileObject javaClassFileObject = (JavaClassFileObject) file;
            return javaClassFileObject.getAllClassPath();
        }


        return standardJavaFileManager.inferBinaryName(location, file);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return false;
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return true;
    }

    @Override
    public boolean hasLocation(Location location) {
        return true;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return null;
    }

    /**
     * 编译结束后的回调
     *
     * @param location
     * @param className
     * @param kind
     * @param fileObject
     * @return
     * @throws IOException
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject fileObject) throws IOException {
        if (!(fileObject instanceof JavaSourceFileObject)) {
            throw new IllegalArgumentException("非法的编译类型 [" + fileObject.getName() + "] ");
        }

        JavaSourceFileObject javaSource = (JavaSourceFileObject) fileObject;
        JavaClassFileObject result = new JavaClassFileObject(className);
        javaSource.setClassObject(result);

        //编译好的文件对象输出到 classCache 中, 这样后面的就可以直接使用编译好的文件
        addCache(classCache, result.getClassPath(), result);
        return result;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return null;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return null;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {
        System.out.println("");
    }

    @Override
    public int isSupportedOption(String option) {
        return 0;
    }

    private <K> void addCache(Map<String, List<K>> cache, String key, K value) {
        List<K> ks = cache.computeIfAbsent(key, k -> new ArrayList<>());
        ks.add(value);
    }

    /**
     * 获取编译完后的 JavaFileObject 对象.
     *
     * @return
     */
    public Optional<JavaFileObject> getCompilerFile(String allPath) {
        Pair<String, String> pathAndName = StringUtils.separatePath(allPath, ".");
        List<JavaFileObject> pathFile = classCache.get(pathAndName.getKey());
        if (pathFile == null || pathFile.size() == 0) {
            return Optional.empty();
        }

        String allPathAndSuffix = allPath + ".class";
        for (JavaFileObject javaFileObject : pathFile) {
            if (allPathAndSuffix.equals(javaFileObject.getName())) {
                return Optional.of(javaFileObject);
            }
        }
        return Optional.empty();
    }

    public boolean isExistClass(String allPath) {
        return getCompilerFile(allPath).isPresent();
    }

}
