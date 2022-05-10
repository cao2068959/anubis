package org.chy.anubis.dynamic.compiler;

import javax.tools.*;
import java.io.IOException;
import java.util.*;


import static org.chy.anubis.Constant.TREASURY_BASE_PATH;

/**
 * 为了兼容 JDK9以上的模块化编译
 */
public class AnubisJavaModuleFileManager extends AnubisJavaFileManager {

    public AnubisJavaModuleFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
    }


    @Override
    public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service) throws IOException {
        return standardJavaFileManager.getServiceLoader(location, service);
    }


    public Location getLocationForModule(Location location, String moduleName) throws IOException {
        return standardJavaFileManager.getLocationForModule(location, moduleName);
    }


    public Location getLocationForModule(Location location, JavaFileObject fo) throws IOException {
        return standardJavaFileManager.getLocationForModule(location, fo);
    }


    public String inferModuleName(Location location) throws IOException {
        return standardJavaFileManager.inferModuleName(location);
    }


    public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
        if (StandardLocation.SYSTEM_MODULES == location) {
            return standardJavaFileManager.listLocationsForModules(location);
        }

        return new HashSet<>();
    }

}
