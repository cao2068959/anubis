package org.chy.anubis.compiler;


import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.Getter;
import lombok.Setter;
import org.chy.anubis.entity.JavaFile;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class JavaSourceFileObject extends SimpleJavaFileObject {

    private final static String JAVA_SUFFIX = ".java";
    private String javaSourceData;

    /**
     * 编译好的 class对象
     */
    @Setter
    @Getter
    private JavaClassFileObject classObject;


    public JavaSourceFileObject(String className, String javaSourceData) {
        super(createURI(className + JAVA_SUFFIX), Kind.SOURCE);
        this.javaSourceData = javaSourceData;
    }

    public JavaSourceFileObject(JavaFile javaFile) {
        this(javaFile.getName(), javaFile.getBlobData());
    }

    private static URI createURI(String className) {
        try {
            return new URI(className);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return javaSourceData;
    }

}

