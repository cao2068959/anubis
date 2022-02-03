package org.chy.anubis.warehouse.api;

import org.chy.anubis.warehouse.api.dos.FileBlobDescribeInfoDO;
import org.chy.anubis.warehouse.api.dos.FileDescribeInfoDO;
import org.chy.anubis.warehouse.api.dos.JsonResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface AnubisServiceApi {


    @GET("/api/anubis/filewarehouse/filelist")
    Call<JsonResult<List<FileDescribeInfoDO>>> findFileList(@Query("path") String path);



    @GET("/api/anubis/filewarehouse/content")
    Call<JsonResult<FileBlobDescribeInfoDO>> findFileContent(@Query("path") String path);

}
