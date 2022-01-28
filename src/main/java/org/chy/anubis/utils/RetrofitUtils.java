package org.chy.anubis.utils;

import org.chy.anubis.exception.HttpRequestException;
import org.chy.anubis.warehouse.api.dos.JsonResult;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class RetrofitUtils {


    public static <T> T exec(Call<T> call) {
        try {
            Response<T> result = call.execute();
            if (!result.isSuccessful()) {
                throw new HttpRequestException("请求[" + call.request().toString() + "]失败 code:[" + result.code() + "] errorMsg:[" + result.errorBody() + "]");
            }
            return result.body();
        } catch (IOException e) {
            throw new HttpRequestException("请求[" + call.request().toString() + "]失败", e);
        }
    }

        public static <T> T execJsonResult(Call<JsonResult<T>> call) {
        JsonResult<T> jsonResult = exec(call);
        if (!jsonResult.isSuccess()) {
            throw new HttpRequestException("请求[" + call.request().toString() + "]失败 errorMsg:[" + jsonResult.getMsg() + "]");
        }
        return jsonResult.getData();
    }


}
