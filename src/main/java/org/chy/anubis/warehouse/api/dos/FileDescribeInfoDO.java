package org.chy.anubis.warehouse.api.dos;

import org.chy.anubis.enums.FileType;

public class FileDescribeInfoDO {

    String name;
    String url;
    FileType fileType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
