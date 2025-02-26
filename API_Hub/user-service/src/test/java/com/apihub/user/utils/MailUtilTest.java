package com.apihub.user.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailUtilTest {

    @Autowired
    MailUtil mailUtil;


    // 测试发送邮件
    @Test
    void testSendEmail() {
        mailUtil.sendMailMessage("1355609295@qq.com", "Test Subject", "This is a test email.");
    }
}
