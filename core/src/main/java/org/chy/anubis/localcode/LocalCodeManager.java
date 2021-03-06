package org.chy.anubis.localcode;

import org.chy.anubis.Constant;
import org.chy.anubis.entity.FileBaseInfo;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.enums.FileType;
import org.chy.anubis.exception.NoSuchFieldException;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.FileUtils;
import org.chy.anubis.warehouse.WarehouseHolder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * 自动生成的代码管理器
 */
public class LocalCodeManager {
    public static LocalCodeManager instance = new LocalCodeManager();
    private final String rootPath;
    private Map<String, FileInfo> cache = new ConcurrentHashMap<>();


    public LocalCodeManager() {
        String path = PropertyContextHolder.context.anubis.localcode.path;
        if (path == null) {
            path = generatedRootPath();
        }
        this.rootPath = path + "/" + Constant.TREASURY_BASE_PATH.replace(".", "/");
        ;
        //先把对应的目录给创建好
        FileUtils.initDir(rootPath);

    }


    private String generatedRootPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = path.endsWith("/") ? path.substring(0, path.length() - 2) : path;
        path = path.substring(0, path.lastIndexOf("/")) + "/" + "generated-anubis";
        return path;
    }

    /**
     * 获取对应的java源文件
     *
     * @param filePath
     * @return
     */
    public JavaFile getJavaSource(String filePath) {
        Optional<FileInfo> fileInfoOptional = getCacheFileOrDownload(filePath);
        if (!fileInfoOptional.isPresent()) {
            throw new NoSuchFieldException("本地/远程 无法获取文件 [" + filePath + "]");
        }
        FileInfo fileInfo = fileInfoOptional.get();
        if (fileInfo instanceof JavaFile) {
            return (JavaFile) fileInfo;
        }
        throw new NoSuchFieldException("获取java类源文件[" + filePath + "] 失败, 该文件格式不正确");
    }

    /**
     * 获取一个包下的所有 java文件
     *
     * @param packagePath
     * @return
     */
    public List<JavaFile> getJavaSourceByPackage(String packagePath) {
        List<FileBaseInfo> fileBaseInfoList = WarehouseHolder.warehouse.getFileBaseInfoList(packagePath);
        List<JavaFile> result = new ArrayList<>();
        for (FileBaseInfo fileBaseInfo : fileBaseInfoList) {
            //不是文件类型,那么就不浪费精力了
            if (fileBaseInfo.getFileType() != FileType.BLOB) {
                continue;
            }
            //从缓存或者远程仓库获取文件
            Optional<FileInfo> fileInfoOptional = getCacheFileOrDownload(fileBaseInfo.getUrl());
            if (!fileInfoOptional.isPresent()) {
                continue;
            }
            FileInfo fileInfo = fileInfoOptional.get();
            if (fileInfo instanceof JavaFile) {
                result.add((JavaFile) fileInfo);
            }

        }
        return result;
    }

    /**
     * 获取对应路径的文件,如果不存在就下载对应的文件
     *
     * @param filePath
     */
    public Optional<FileInfo> getCacheFileOrDownload(String filePath) {
        return getCacheFileOrLoad(filePath, this::downloadFile);
    }


    public Optional<FileInfo> getCacheFileOrLoad(String filePath, Function<String, Optional<String>> loadFun) {
        String localFilePath = rootPath + FileUtils.filePathHandler(filePath);
        FileInfo cacheFileInfo = cache.get(filePath);
        if (cacheFileInfo != null) {
            return Optional.of(cacheFileInfo);
        }

        Pair<String, String> pathAndName = FileUtils.separatePath(filePath);
        String fileName = pathAndName.getValue();
        if (fileName == null) {
            return Optional.empty();
        }

        //是否从本地缓存文件中读取代码
        Boolean localCodeRefresh = PropertyContextHolder.context.anubis.localcode.refresh;
        Optional<String> fileContent = localCodeRefresh ? Optional.empty() : FileUtils.readFile(localFilePath);
        //本地没有这个文件,那么从某个地方获取这个问题
        if (!fileContent.isPresent()) {
            fileContent = loadFun.apply(filePath);
            fileContent.ifPresent(content -> {
                //把拉到的文件写入硬盘
                FileUtils.writeFile(localFilePath, content.getBytes(StandardCharsets.UTF_8));
            });
        }

        Optional<FileInfo> result = fileContent.map(content -> {
            FileInfo fileInfo = FileUtils.getFileSuffix(fileName).map(fileSuffix -> {
                if ("java".equals(fileSuffix)) {
                    return new JavaFile();
                }
                return new FileInfo();
            }).orElse(new FileInfo());
            fileInfo.setFileType(FileType.BLOB);
            fileInfo.setName(fileName);
            fileInfo.setBlobData(content);
            fileInfo.setUrl(filePath);
            return fileInfo;
        });

        result.ifPresent((value) -> {
            //写入缓存
            cache.put(filePath, value);
        });
        return result;

    }


    private Optional<String> downloadFile(String filePath) {
        filePath = FileUtils.filePathHandler(filePath);

        Optional<FileInfo> fileInfoOptional = WarehouseHolder.warehouse.getFileInfo(filePath);
        //远程也是什么都没拉到
        if (!fileInfoOptional.isPresent()) {
            return Optional.empty();
        }
        FileInfo fileInfo = fileInfoOptional.get();
        byte[] decodeData = fileInfo.getBlobData().getBytes(StandardCharsets.UTF_8);
        return Optional.of(new String(decodeData, StandardCharsets.UTF_8));
    }


}
