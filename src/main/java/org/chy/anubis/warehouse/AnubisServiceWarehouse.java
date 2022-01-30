package org.chy.anubis.warehouse;

import com.google.gson.Gson;
import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.enums.CaseSourceType;
import org.chy.anubis.enums.FileType;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.RetrofitUtils;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.warehouse.api.AnubisServiceApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.chy.anubis.Constant.TESTCASE_PATH;
import static org.chy.anubis.Constant.TESTCASE_TEMPLATE_PATH;

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

    private String getTestCasePath(CaseSourceType caseSourceType, String algorithmName) {
        algorithmName = StringUtils.humpToLine(algorithmName);
        StringBuilder result = new StringBuilder();
        result.append(TESTCASE_PATH).append("/").append(caseSourceType.getName()).append("/")
                .append(algorithmName).append("/").append(TESTCASE_TEMPLATE_PATH);
        return result.toString();
    }

}
