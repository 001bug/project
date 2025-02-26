package com.apihub.sdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.apihub.sdk.exception.ApiException;
import com.apihub.sdk.exception.ErrorCode;
import com.apihub.sdk.model.dto.ExecuteCodeRequest;
import com.apihub.sdk.model.dto.PictureQueryRequest;
import com.google.gson.Gson;

public class ApiHubSelfApiClient {

    private static final String SELF_API_HOST = "http://8.130.139.252:8201/api";

    private final String token;

    public ApiHubSelfApiClient(String token) {
        this.token = token;
    }

    public String PictureApi(PictureQueryRequest pictureQueryRequest) throws ApiException {
        tokenCheck();
        Gson gson = new Gson();
        String json = gson.toJson(pictureQueryRequest);
        HttpResponse httpResponse = HttpRequest.post(SELF_API_HOST + "/picture/list/page/vo")
                .header("apiToken", token)
                .body(json)
                .timeout(5000)
                .execute();

        resCheck(httpResponse.getStatus());
        return httpResponse.body();
    }

    public String OJSandBox(ExecuteCodeRequest executeCodeRequest) throws ApiException {
        tokenCheck();
        Gson gson = new Gson();
        String json = gson.toJson(executeCodeRequest);
        HttpResponse httpResponse = HttpRequest.post(SELF_API_HOST + "/sandbox/executeCode/java")
                .header("apiToken", token)
                .body(json)
                .timeout(5000)
                .execute();

        resCheck(httpResponse.getStatus());
        return httpResponse.body();
    }

    private void tokenCheck() throws ApiException {
        if (token == null) {
            throw new ApiException(ErrorCode.PARAMS_ERROR);
        }
    }

    private void resCheck(int status) throws ApiException {
        if (status == 403) {
            throw new ApiException(ErrorCode.NO_AUTH_ERROR, "accessKey与secretKey不匹配");
        }
        if (status < 500 && status >= 400) {
            throw new ApiException(ErrorCode.FORBIDDEN_ERROR, "请稍后重试");
        }
        if (status >= 500) {
            throw new ApiException(ErrorCode.OPERATION_ERROR);
        }
    }
}
