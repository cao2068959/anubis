package org.chy.anubis.entity;

import lombok.Data;
import org.chy.anubis.enums.FileType;

@Data
public class FileInfo {

    String name;
    String url;
    FileType fileType;
    String blobData;
}
