package org.chy.anubis.localcode;

import com.sun.tools.javac.api.JavacTool;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.entity.JavaFile;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.enums.FileType;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.FileUtils;
import org.chy.anubis.utils.WarehouseUtils;
import org.chy.anubis.warehouse.WarehouseHolder;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import static org.chy.anubis.Constant.ALGORITHM_INTERFACE_NAME;

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
     * 获取对应路径的代码文件,如果不存在就下载对应的文件
     *
     * @param filePath
     */
    public Optional<FileInfo> getLocalCodeOrDownload(String filePath) {
        String localFilePath = rootPath + FileUtils.filePathHandler(filePath);
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

        return fileContent.map(content -> {
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
