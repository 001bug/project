package com.apihub.user.controller;

import cn.hutool.http.HttpRequest;
import com.apihub.user.model.dto.aouth.GiteeTokenRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@Slf4j
@SpringBootTest
public class OauthTest {

    @Value("${justauth.type.GITEE.client-id}")
    private String clientId;

    @Value("${justauth.type.GITEE.client-secret}")
    private String clientSecret;

    @Test
    public void test() {
        GiteeTokenRequest giteeTokenRequest = new GiteeTokenRequest();
        giteeTokenRequest.setClient_id(clientId);
        giteeTokenRequest.setClient_secret(clientSecret);
        giteeTokenRequest.setUsername("1355609295@qq.com");
        giteeTokenRequest.setPassword("jimmy2333");
        giteeTokenRequest.setScope("user_info");
        giteeTokenRequest.setGrant_type("password");

        Gson gson = new Gson();
        String giteeTokenRequestJson = gson.toJson(giteeTokenRequest);

        String result1 = HttpRequest.post("https://gitee.com/oauth/token")
                .body(giteeTokenRequestJson).timeout(10000)
                .execute().body();

        log.info(result1);
        log.info("--------------------------------------");

        Map<String, String>
                map = gson.fromJson(result1, new TypeToken<Map<String, String>>() {
        }.getType());
        String access_token = map.get("access_token");

        log.info(access_token);
        log.info("--------------------------------------");

        String result2 = HttpRequest.post("https://gitee.com/api/v5/user?access_token=" + access_token)
                .timeout(10000)
                .execute().body();
        log.info(result2);
    }
}
