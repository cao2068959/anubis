package org.chy.anubis.ast;

import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;


/**
 * 整个 java 源文件的类描述对象.
 */
public class JavaSourceClassDefinition {

    char[] datas;

    //类的包路径
    @Getter
    @Setter
    String packagePath;

    List<String> importContent = new ArrayList<>();

    @Getter
    @Setter
    String className;

    public JavaSourceClassDefinition(char[] data) {
        this.datas = data;
        analyzing();
    }

    /**
     * 分析整个源码文件
     */
    private void analyzing() {
        Resolver resolver = new Resolver(this);

        resolver.parseJavaInfo();

    }


    public void addImportContent(String data){
        importContent.add(data);
    }


}