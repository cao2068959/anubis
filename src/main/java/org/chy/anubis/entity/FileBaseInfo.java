package org.chy.anubis.entity;

import lombok.Data;
import org.chy.anubis.enums.FileType;

@Data
public class FileBaseInfo {

    String name;
    String url;
    FileType fileType;
}
