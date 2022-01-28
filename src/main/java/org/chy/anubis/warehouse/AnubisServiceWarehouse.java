package org.chy.anubis.warehouse;

import com.google.gson.Gson;
import org.chy.anubis.enums.CaseSourceType;
import org.chy.anubis.property.PropertyContextHolder;
import org.chy.anubis.utils.ReflectUtils;
import org.chy.anubis.utils.RetrofitUtils;
import org.chy.anubis.utils.StringUtils;
import org.chy.anubis.warehouse.api.AnubisServiceApi;
import org.chy.anubis.warehouse.api.dos.FileDescribeInfoDO;
import org.chy.anubis.warehouse.api.dos.JsonResult;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class AnubisServiceWarehouse implements Warehouse {

    private final AnubisServiceApi anubisServiceApi;

    public AnubisServiceWarehouse() {
        String host = PropertyContextHolder.getAnubisProperty().treasury.anubisService.host;
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(new Gson())).baseUrl(host).build();
        this.anubisServiceApi = retrofit.create(AnubisServiceApi.class);
    }

    @Override
    public void getCaseCatalog(CaseSourceType caseSourceType, String algorithmName) {


        List<FileDescribeInfoDO> fileDescribeInfoDOS = RetrofitUtils.execJsonResult(anubisServiceApi.findFileList("utils"));

        System.out.println(fileDescribeInfoDOS);
    }

    private String getTestCasePath(CaseSourceType caseSourceType, String algorithmName) {
        algorithmName = StringUtils.humpToLine(algorithmName);

        return null;
    }


}
