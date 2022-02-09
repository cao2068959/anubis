package org.chy.anubis.compiler;


import lombok.Getter;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class JavaClassFileObject extends SimpleJavaFileObject {

    private final static String CLASS_SUFFIX = ".class";
    @Getter
    private ByteArrayOutputStream dataStream;

    public JavaClassFileObject(String className) {
        super(createURI(className + CLASS_SUFFIX), Kind.CLASS);
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

