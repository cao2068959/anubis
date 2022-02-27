package org.chy.anubis.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.chy.anubis.exception.JavaParserException;



public class JavaParserContainer {

    static JavaParser javaParser = new JavaParser();

    public static JavaSourceParser parse(String data){
        ParseResult<CompilationUnit> parseData = javaParser.parse(data);
        CompilationUnit parseResult = parseData.getResult().orElseThrow(() -> new JavaParserException(parseData.getProblems()));
        return new JavaSourceParser(parseResult);
    }

}
