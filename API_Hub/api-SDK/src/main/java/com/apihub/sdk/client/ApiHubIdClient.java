package com.apihub.sdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.apihub.sdk.exception.ApiException;
import com.apihub.sdk.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;

import static com.apihub.sdk.utils.ApiSignUtils.genSign;

public class ApiHubIdClient {

    private static final String GATEWAY_HOST = "http://localhost:8100/api";

    private final String accessKey;

    private final String secretKey;

    public ApiHubIdClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("body", body);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        hashMap.put("timestamp", timestamp);
        hashMap.put("secretSign", genSign(timestamp, secretKey));
        return hashMap;
    }


    public String interfaceIdByGet(Long interfaceId, String body) throws ApiException {
        paramCheck();
        //数据拼接
        String interfaceIdURL = URLUtil.decode(String.valueOf(interfaceId), CharsetUtil.CHARSET_UTF_8);
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);

        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/apiService/get" +
                        "?InterfaceId=" + interfaceIdURL + "&body=" + requestBody)
                .addHeaders(getHeaderMap(""))
                .execute();
        String result = httpResponse.body();
        resCheck(httpResponse.getStatus());
//        System.out.println(result);
        return result;
    }

    public String interfaceIdByPost(Long interfaceId, String body) throws ApiException {
        paramCheck();

        String interfaceIdURL = URLUtil.decode(String.valueOf(interfaceId), CharsetUtil.CHARSET_UTF_8);
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);

        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/apiService/post" +
                        "?InterfaceId=" + interfaceIdURL + "&body=" + requestBody)
                .addHeaders(getHeaderMap(""))
                .execute();
        String result = httpResponse.body();
        //System.out.println(httpResponse.getStatus());
        resCheck(httpResponse.getStatus());
//        System.out.println(result);
        return result;
    }

    private void paramCheck() throws ApiException {
        if (this.accessKey == null || this.accessKey.length() < 30) {
            throw new ApiException(ErrorCode.PARAMS_ERROR, "accessKey为空");
        }
        if (this.secretKey == null || this.secretKey.length() < 30) {
            throw new ApiException(ErrorCode.PARAMS_ERROR, "secretKey为空");
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
