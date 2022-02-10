package org.chy.anubis.compiler;


import lombok.Getter;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.utils.StringUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class JavaClassFileObject extends SimpleJavaFileObject {

    private final static String CLASS_SUFFIX = ".class";
    @Getter
    private ByteArrayOutputStream dataStream;

    @Getter
    private String className;
    @Getter
    private String classPath;

    public JavaClassFileObject(String classAllPath) {
        super(createURI(classAllPath + CLASS_SUFFIX), Kind.CLASS);
        Pair<String, String> pathAndName = StringUtils.separatePath(classAllPath, ".");
        this.classPath = pathAndName.getKey();
        this.className = pathAndName.getValue();
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
    public OutputStream openOutputStream() throws IOException {
        if (dataStream == null) {
            dataStream = new ByteArrayOutputStream();
        }
        return dataStream;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(dataStream.toByteArray());
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new InputStreamReader(openInputStream());
    }
}

