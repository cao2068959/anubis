package org.chy.anubis.localcode;

import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.property.mapping.AnubisProperty;

/**
 * 自动生成的代码管理器
 */
public class LocalCodeManager {
    public static LocalCodeManager instance = new LocalCodeManager();
    private final String rootPath;


    public LocalCodeManager() {
        String path = PropertyContextHolder.context.anubis.localcode.path;
        if (path == null) {
            path = generatedRootPath();
        }
        this.rootPath = path;
    }


    private String generatedRootPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = path.endsWith("/") ? path.substring(0, path.length() - 2) : path;
        path = path.substring(0, path.lastIndexOf("/")) + "/" + "generated-anubis";
        return path;
    }




}
