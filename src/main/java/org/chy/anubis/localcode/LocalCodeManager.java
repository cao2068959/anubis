package org.chy.anubis.localcode;

import org.apache.commons.io.FileExistsException;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.enums.FileType;
import org.chy.anubis.exception.NoSuchFieldException;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.FileUtils;
import org.chy.anubis.warehouse.WarehouseHolder;


import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


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
        this.rootPath = path;

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
     * @param filePath
     * @return
     */
    public JavaFile getJavaSource(String filePath) {
        Optional<FileInfo> fileInfoOptional = getCacheFileOrDownload(filePath);
        if (!fileInfoOptional.isPresent()) {
            throw new NoSuchFieldException("本地/远程 无法获取文件 [" + filePath + "]");
        }
        FileInfo fileInfo = fileInfoOptional.get();
        if (fileInfo instanceof JavaFile){
            return (JavaFile) fileInfo;
        }
        throw new NoSuchFieldException("获取java类源文件[" + filePath + "] 失败, 该文件格式不正确");
    }

    /**
     * 获取对应路径的文件,如果不存在就下载对应的文件
     *
     * @param filePath
     */
    public Optional<FileInfo> getCacheFileOrDownload(String filePath) {
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

        Optional<String> fileContent = FileUtils.readFile(localFilePath);
        //本地没有这个文件,那么开始下载这个文件
        if (!fileContent.isPresent()) {
            fileContent = downloadFile(filePath);
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

        result.ifPresent((value) -> cache.put(filePath, value));
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
        //把拉到的文件写入硬盘
        FileUtils.writeFile(rootPath + filePath, decodeData);
        return Optional.of(new String(decodeData, StandardCharsets.UTF_8));
    }


}
