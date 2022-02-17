package org.chy.anubis.warehouse;

import com.google.gson.Gson;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.enums.CaseSourceType;
import org.chy.anubis.enums.FileType;
import org.chy.anubis.log.Logger;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.RetrofitUtils;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.warehouse.api.AnubisServiceApi;
import org.chy.anubis.warehouse.api.dos.FileBlobDescribeInfoDO;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.chy.anubis.utils.WarehouseUtils.getTestCasePath;

public class AnubisServiceWarehouse implements Warehouse {

    private final AnubisServiceApi anubisServiceApi;

    public AnubisServiceWarehouse() {
        String host = PropertyContextHolder.getAnubisProperty().treasury.anubisService.host;
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(new Gson())).baseUrl(host).build();
        this.anubisServiceApi = retrofit.create(AnubisServiceApi.class);
    }

    @Override
    public List<CaseBriefInfo> getCaseCatalog(CaseSourceType caseSourceType, String algorithmName) {
        String testCasePath = getTestCasePath(caseSourceType, algorithmName);
        return RetrofitUtils.execJsonResult(anubisServiceApi.findFileList(testCasePath)).stream()
                .filter(fileDescribeInfoDO -> fileDescribeInfoDO.getFileType() == FileType.DIRECTORY)
                .map(fileDescribeInfoDO -> CaseBriefInfo.builder().name(fileDescribeInfoDO.getName()).url(fileDescribeInfoDO.getUrl()).build())
                .collect(Collectors.toList());
    }

    /**
     * 根据文件路径获取文件内容
     *
     * @return
     */
    @Override
    public Optional<FileInfo> getFileInfo(String path) {
        try {
            FileBlobDescribeInfoDO fileBlobDescribeInfoDO = RetrofitUtils.execJsonResult(anubisServiceApi.findFileContent(path));
            return Optional.ofNullable(fileBlobDescribeInfoDO).map(fb -> {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setUrl(fb.getUrl());
                fileInfo.setFileType(fb.getFileType());
                fileInfo.setName(fb.getName());
                fileInfo.setBlobData(StringUtils.base64Decode(fb.getBlobData()));
                return fileInfo;
            });
        } catch (Exception e) {
            Logger.error("文件[" + path + "] 获取失败");
        }
        return Optional.empty();
    }


}
