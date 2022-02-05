package org.chy.anubis.compiler;


import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class StringJavaFileObject extends SimpleJavaFileObject {

    private final static String class_suffix = ".class";
    private final static String java_suffix = ".java";
    private String javaSourceData;

    public StringJavaFileObject(URI uri, Kind kind, String javaSourceData) {
        super(uri, kind);
        this.javaSourceData = javaSourceData;
    }


    private URI createURI(String className){

    }

}

