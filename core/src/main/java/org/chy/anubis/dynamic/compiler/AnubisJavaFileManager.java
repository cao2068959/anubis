package org.chy.anubis.dynamic.compiler;


import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.utils.WarehouseUtils;

import javax.tools.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.chy.anubis.Constant.TREASURY_BASE_PATH;

public class AnubisJavaFileManager implements JavaFileManager {

    StandardJavaFileManager standardJavaFileManager;

    //key:对应的包路径 value:对应这个路径下的所有class文件
    Map<String, List<JavaFileObject>> classCache = new ConcurrentHashMap<>();
    //源码文件的缓存
    Map<String, List<JavaFileObject>> sourceCache = new ConcurrentHashMap<>();
    Set<URL> processsorPaths = new HashSet<>();

    public AnubisJavaFileManager(StandardJavaFileManager standardJavaFileManager) {
        this.standardJavaFileManager = standardJavaFileManager;
        initClasspath();
    }

    @SneakyThrows
    private void initClasspath() {
        Class<?> lombok = Class.forName("lombok.Data");
        ProtectionDomain domain = lombok.getProtectionDomain();
        if (domain != null && domain.getCodeSource() != null) {
            this.addProcessorPath(domain.getCodeSource().getLocation());
        }
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        if (location == StandardLocation.ANNOTATION_PROCESSOR_PATH) {
            return new URLClassLoader(this.processsorPaths.toArray(new URL[0]),
                    this.standardJavaFileManager.getClass().getClassLoader());
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (packageName.startsWith(TREASURY_BASE_PATH)) {
            List<JavaFileObject> result = new ArrayList<>();
            if (kinds.contains(JavaFileObject.Kind.CLASS)) {
                result.addAll(findClassJavaFileObject(location, packageName, kinds, recurse));
            }

            //查找编译所依赖的 源码文件
            if (kinds.contains(JavaFileObject.Kind.SOURCE)) {
                result.addAll(findSourceJavaFileObject(location, packageName, kinds, recurse));
            }
            return result;
        }
        return standardJavaFileManager.list(location, packageName, kinds, recurse);
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
    protected List<JavaFileObject> findClassJavaFileObject(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        //先从缓存中查找
        List<JavaFileObject> result = classCache.get(packageName);
        if (result != null) {
            return result;
        }

        result = new ArrayList<>();
        //查找的路径是 anubis-treasury 项目下面的,如果缓存没找到,说明没开始编译就不再去下面查找了
        if (packageName.startsWith(TREASURY_BASE_PATH)) {
            classCache.put(packageName, result);
            return result;
        }

        //去系统路径查找
        Iterable<JavaFileObject> localClass = standardJavaFileManager.list(location, packageName, kinds, recurse);

        for (JavaFileObject javaFileObject : localClass) {
            result.add(javaFileObject);
        }
        return result;
    }

    /**
     * 查询在对应的路径下面有没可以读取到java源文件, 编译时候可能会依赖到
     */
    protected List<JavaFileObject> findSourceJavaFileObject(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        //先从缓存中获取
        List<JavaFileObject> result = sourceCache.get(packageName);
        if (result != null) {
            return result;
        }

        String packagePath = WarehouseUtils.javaPackagePathToRemotePath(packageName, false);
        if (packagePath == null) {
            return new ArrayList<>();
        }
        //去远程或者本地获取对应的源码文件
        List<JavaFile> javaSourceByPackage = LocalCodeManager.instance.getJavaSourceByPackage(packagePath);
        result = javaSourceByPackage.stream().map(JavaSourceFileObject::new).collect(Collectors.toList());
        sourceCache.put(packageName, result);
        return result;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof JavaClassFileObject) {
            JavaClassFileObject javaClassFileObject = (JavaClassFileObject) file;
            return javaClassFileObject.getAllClassPath();
        }

        if (file instanceof JavaSourceFileObject) {
            JavaSourceFileObject javaSourceFileObject = (JavaSourceFileObject) file;
            return javaSourceFileObject.getAllClassPath();
        }

        return standardJavaFileManager.inferBinaryName(location, file);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return standardJavaFileManager.isSameFile(a, b);
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return standardJavaFileManager.handleOption(current, remaining);
    }

    @Override
    public boolean hasLocation(Location location) {
        return standardJavaFileManager.hasLocation(location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return standardJavaFileManager.getJavaFileForInput(location, className, kind);
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
        return standardJavaFileManager.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return standardJavaFileManager.getFileForOutput(location, packageName, relativeName, sibling);
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

    public void addProcessorPath(URL processorUrl) {
        this.processsorPaths.add(processorUrl);
    }

    @SneakyThrows
    public Set<String> findAnnotationProcessor() {
        Set<String> processors = new HashSet<>();
        if (!this.processsorPaths.isEmpty()) {
            ClassLoader apClassloader = getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH);
            Enumeration<URL> resources = apClassloader.getResources("META-INF/services/javax.annotation.processing.Processor");
            while (resources.hasMoreElements()) {
                try (InputStream in = resources.nextElement().openStream()) {
                    processors.addAll(Arrays.asList(IOUtils.toString(in).split("\n")));
                }
            }
        }
        return processors;
    }
}
