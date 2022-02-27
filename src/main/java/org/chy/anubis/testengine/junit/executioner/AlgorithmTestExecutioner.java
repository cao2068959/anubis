package org.chy.anubis.testengine.junit.executioner;

import com.sun.tools.javac.api.JavacTool;
import lombok.SneakyThrows;
import org.chy.anubis.compiler.AnubisJavaFileManager;
import org.chy.anubis.compiler.CharSequenceJavaFileObject;
import org.chy.anubis.compiler.JavaSourceFileObject;
import org.chy.anubis.compiler.JdkDynamicCompileJavaFileManager;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.localcode.LocalCodeManager;
import org.chy.anubis.log.Logger;
import org.chy.anubis.testengine.junit.AlgorithmMethodDefinition;
import org.chy.anubis.testengine.junit.descriptor.AlgorithmTestDescriptor;
import org.chy.anubis.testengine.junit.descriptor.CaseTestDescriptor;
import org.chy.anubis.utils.WarehouseUtils;
import org.chy.anubis.warehouse.WarehouseHolder;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.chy.anubis.Constant.ALGORITHM_INTERFACE_NAME;

public class AlgorithmTestExecutioner extends CommonExecutioner<AlgorithmTestDescriptor> {


    private final AlgorithmMethodDefinition definition;

    public AlgorithmTestExecutioner(EngineExecutionListener listener, AlgorithmTestDescriptor testDescriptor) {
        super(listener, testDescriptor);
        this.definition = testDescriptor.getAlgorithmMethodDefinition();
    }

    public void start() {
        run(this::doStart);
    }

    public void doStart(AlgorithmTestDescriptor algorithmTestDescriptor) {
        Set<CaseTestDescriptor> testChildren = algorithmTestDescriptor.getTestChildren();
        if (testChildren == null || testChildren.isEmpty()) {
            Logger.info("没有获取到该算法下面要执行的测试用例");
            return;
        }

        findAlgorithmInterface();
        String allCaseName = testChildren.stream().map(CaseTestDescriptor::getDisplayName).collect(Collectors.joining(" , "));
        Logger.info("将要执行的测试用例为: [" + allCaseName + "]");


        //遍历这个算法下面所有的测试案例
        algorithmTestDescriptor.foreachChild(caseTestDescriptor -> {
            CaseTestExecutioner executioner = new CaseTestExecutioner(listener, caseTestDescriptor);
            //开始执行这个测试用例
            executioner.start();
        });
    }

    /**
     * 获取这个算法在远程仓库端定义的算法接口
     */
    private Optional<FileInfo> findAlgorithmInterface() {
        Optional<FileInfo> algorithmInterface = LocalCodeManager.instance.getLocalCodeOrDownload(WarehouseUtils
                .getPathFromAlgorithmPath(definition.getCaseSourceType(), definition.getAlgorithmName(),
                        ALGORITHM_INTERFACE_NAME));

        algorithmInterface.ifPresent(fileInfo -> {
            xxx(fileInfo);
        });
        return algorithmInterface;

    }

    @SneakyThrows
    public void xxx(FileInfo data) {
        JavaCompiler javaCompiler = JavacTool.create();
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(diagnostic -> {
            System.out.println(diagnostic);
        }, Locale.CHINESE, StandardCharsets.UTF_8);

        File file2 = new File("/Users/bignosecat/IdeaProjects/netty/anubis/src/test/java/myaaa/testcase/leetcode/two_sum/Algorithm.java");
        Iterable<? extends JavaFileObject> javaFileObjects = standardFileManager.getJavaFileObjects(file2);


        File file = new File("/Users/bignosecat/IdeaProjects/netty/anubis/src/test/java/myaaa/testcase/leetcode/two_sum/xxx.class");
        FileWriter fileOutputStream = new FileWriter(file);

        ArrayList<String> objects = new ArrayList<>();
        objects.add("xxxxx");

        List<JavaFileObject> javaFileObjects1 = new ArrayList<>();
        JavaSourceFileObject jf = new JavaSourceFileObject("Algorithm", data.getBlobData());
        CharSequenceJavaFileObject c = new CharSequenceJavaFileObject("Algorithm", data.getBlobData());

        javaFileObjects1.add(jf);
        AnubisJavaFileManager anubisJavaFileManager = new AnubisJavaFileManager(standardFileManager);

        JdkDynamicCompileJavaFileManager jdkDynamicCompileJavaFileManager = new JdkDynamicCompileJavaFileManager(standardFileManager);

        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, anubisJavaFileManager, null, null, null, javaFileObjects1);
        Boolean call = task.call();

        byte[] byteCode = jf.getClassObject().getDataStream().toByteArray();

        System.out.println(call);

    }

}
