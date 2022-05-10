package org.chy.anubis.entity;

import lombok.Data;
import org.chy.anubis.enums.FileType;

import java.util.Base64;

@Data
public class FileInfo extends FileBaseInfo {

    String blobData;

}
