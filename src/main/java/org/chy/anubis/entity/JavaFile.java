package org.chy.anubis.entity;

import org.chy.anubis.ast.JavaSourceClassDefinition;

public class JavaFile extends FileInfo {

    /**
     * java的全路径如: org.chy.anubis.entity.FileInfo
     */
    private String javaPath;

    /**
     * java中的类名如 JavaFile 没有后缀
     */
    private String javaClassName;

    /**
     * 整个java文件解析后放这里面
     */
    private JavaSourceClassDefinition javaSourceClassDefinition;

    @Override
    public void setBlobData(String blobData) {
        super.setBlobData(blobData);
        this.javaSourceClassDefinition = new JavaSourceClassDefinition(blobData.toCharArray());
    }




}
