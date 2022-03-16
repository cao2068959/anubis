package org.chy.anubis.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> parse = javaParser.parse(new File("/Users/bignosecat/IdeaProjects/netty/anubis/src/main/java/org/chy/anubis/ast/Resolver.java"));
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        parse.getResult().get().getType(1);
        System.out.println(parse);
    }

}
