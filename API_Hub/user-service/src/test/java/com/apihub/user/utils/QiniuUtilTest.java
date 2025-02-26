package com.apihub.user.utils;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QiniuUtilTest {


    @Test
    public void getTokenTest() {
        String token = QiniuUtil.getToken("6666");
        System.out.println(token);
    }
}
