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

/**
 * 一个用于与API Hub进行身份验证和数据交互的客户端类。
 * 该类提供了通过GET和POST方法获取接口ID的功能，并处理请求的头部信息和响应状态。
 */
public class ApiHubIdClient {

    // API网关的主机地址
    private static final String GATEWAY_HOST = "http://localhost:8100/api";

    // 访问密钥
    private final String accessKey;

    // 秘密密钥
    private final String secretKey;

    /**
     * 构造函数，初始化客户端的访问密钥和秘密密钥。
     *
     * @param accessKey 访问密钥
     * @param secretKey 秘密密钥
     */
    public ApiHubIdClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 生成请求头部信息的Map。
     *
     * @param body 请求体内容
     * @return 返回包含头部信息的Map
     */
    private Map<String, String> getHeaderMap(String body) {
        // 创建一个HashMap来存储头部信息
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("body", body);
        // 获取当前时间戳
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        hashMap.put("timestamp", timestamp);
        // 生成签名并添加到头部信息
        hashMap.put("secretSign", genSign(timestamp, secretKey));
        return hashMap;
    }


    /**
     * 通过GET方法获取接口ID。
     *
     * @param interfaceId 接口ID
     * @param body 请求体内容
     * @return 返回API响应结果
     */
    public String interfaceIdByGet(Long interfaceId, String body) throws ApiException {
        // 检查参数是否有效
        paramCheck();
        //数据拼接
        String interfaceIdURL = URLUtil.decode(String.valueOf(interfaceId), CharsetUtil.CHARSET_UTF_8);
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);

        // 发送GET请求
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/apiService/get" +
                        "?InterfaceId=" + interfaceIdURL + "&body=" + requestBody)
                .addHeaders(getHeaderMap(""))
                .execute();
        // 获取响应体内容
        String result = httpResponse.body();
        // 检查响应状态
        resCheck(httpResponse.getStatus());
//        System.out.println(result);
        return result;
    }

    /**
     * 通过POST方法获取接口ID。
     *
     * @param interfaceId 接口ID
     * @param body 请求体内容
     * @return 返回API响应结果
     */
    public String interfaceIdByPost(Long interfaceId, String body) throws ApiException {
        // 检查参数是否有效
        paramCheck();

        // 数据拼接
        String interfaceIdURL = URLUtil.decode(String.valueOf(interfaceId), CharsetUtil.CHARSET_UTF_8);
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);

        // 发送POST请求
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/apiService/post" +
                        "?InterfaceId=" + interfaceIdURL + "&body=" + requestBody)
                .addHeaders(getHeaderMap(""))
                .execute();
        // 获取响应体内容
        String result = httpResponse.body();
        //System.out.println(httpResponse.getStatus());
        resCheck(httpResponse.getStatus());
//        System.out.println(result);
        return result;
    }

    /**
     * 检查访问密钥和秘密密钥是否有效。
     *
     */
    private void paramCheck() throws ApiException {
        // 检查accessKey是否为空或长度不足
        if (this.accessKey == null || this.accessKey.length() < 30) {
            throw new ApiException(ErrorCode.PARAMS_ERROR, "accessKey为空");
        }
        // 检查secretKey是否为空或长度不足
        if (this.secretKey == null || this.secretKey.length() < 30) {
            throw new ApiException(ErrorCode.PARAMS_ERROR, "secretKey为空");
        }
    }

    /**
     * 检查API响应状态。
     *
     * @param status HTTP响应状态码
     */
    private void resCheck(int status) throws ApiException {
        // 检查403状态码，表示访问密钥和秘密密钥不匹配
        if (status == 403) {
            throw new ApiException(ErrorCode.NO_AUTH_ERROR, "accessKey与secretKey不匹配");
        }
        // 检查400-499状态码，表示客户端错误
        if (status < 500 && status >= 400) {
            throw new ApiException(ErrorCode.FORBIDDEN_ERROR, "请稍后重试");
        }
        // 检查500及以上状态码，表示服务器错误
        if (status >= 500) {
            throw new ApiException(ErrorCode.OPERATION_ERROR);
        }
    }
}

