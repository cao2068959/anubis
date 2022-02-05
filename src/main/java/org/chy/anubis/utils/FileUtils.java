package org.chy.anubis.utils;

import org.apache.commons.io.IOUtils;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.exception.FileExecException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FileUtils {

    /**
     * 初始化目录
     *
     * @param path
     */
    public static void initDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }

    /**
     * /xxx/xx/ 还是 xxx/xx 统一变成 /xxx/xx 的格式
     *
     * @param path
     */
    public static String filePathHandler(String path) {
        if (path == null || "".equals(path)) {
            return "/";
        }

        if (path.startsWith("/") && !path.endsWith("/")) {
            return path;
        }

        StringBuilder result = new StringBuilder();
        char[] chars = path.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            char c = chars[i];
            if (i == 0 && c != '/') {
                result.append("/");
            }
            if (i == len - 1 && c == '/') {
                break;
            }
            result.append(c);
        }
        return result.toString();
    }

    /**
     * 获取某个路径下文件的内容
     *
     * @param path
     * @return
     */
    public static Optional<String> readFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            throw new FileExecException("文件 [" + path + "] 是一个文件夹不能够读取其内容");
        }

        if (!file.exists()) {
            return Optional.empty();
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            return Optional.of(new String(bytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileExecException("文件 [" + path + "] 读取失败");
        }
    }

    /**
     * 写入文件
     *
     * @param path
     * @param content
     */
    public static void writeFile(String path, byte[] content) {
        if (content == null || content.length == 0) {
            return;
        }

        File file = new File(path);
        //如果文件不存在 那么先去创建这个文件
        if (!file.exists()) {
            Pair<String, String> filePathAndName = separatePath(path);
            //初始化目录
            initDir(filePathAndName.getKey());
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileExecException("文件 [" + path + "] 创建失败", e);
            }
        }

        if (!file.canWrite()) {
            throw new FileExecException("文件 [" + path + "] 没有写权限");
        }


        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            IOUtils.write(content, fileOutputStream);
        } catch (Exception e) {
            throw new FileExecException("文件 [" + path + "] 读取失败", e);
        }


    }

    /**
     * 分离文件和路径 如果是 /结尾的那么就当做文件夹路径,将不会有文件
     *
     * @return key 文件路径 value 文件名
     * /aa/vv/cc/uuu.java ----> key:/aa/vv/cc  value:uuu.java
     */
    public static Pair<String, String> separatePath(String path) {
        if (path == null || "".equals(path)) {
            return Pair.of("/", null);
        }
        int index = path.lastIndexOf("/");
        String filePath = path.substring(0, index);
        String fileName = path.substring(index);
        return Pair.of(filePath, fileName);
    }

    /**
     * 获取文件的后缀
     *
     * @param fileName
     * @return
     */
    public static Optional<String> getFileSuffix(String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        int index = fileName.lastIndexOf(".");
        if (index <= 0 || index == fileName.length() - 1) {
            return Optional.empty();
        }
        return Optional.of(fileName.substring(index + 1));
    }

}
