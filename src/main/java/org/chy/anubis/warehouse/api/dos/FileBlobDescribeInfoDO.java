package org.chy.anubis.warehouse.api.dos;

import lombok.Data;
import org.chy.anubis.enums.FileType;

@Data
public class FileBlobDescribeInfoDO {

    String name;
    String url;
    FileType fileType;
    String blobData;
}
