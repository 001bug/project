package com.apihub.user.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MailUtil {
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sendMailer;

    /*
     * 检查邮件格式
     *
     * */
    public boolean isValidEmail(String email) {
        // static Pattern object, since pattern is fixed
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        // non-static Matcher object because it's created from the input String
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /*
     *  发送单个邮件
     *  to:收件人地址,subject:主题,text:内容
     * */
    @Async
    public boolean sendMailMessage(String to, String subject, String text) {
        log.info("发送邮件===================");
        log.info("to：{}", to);
        log.info("subject：{}", subject);
        log.info("text：{}", text);
        try {
            //true代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人1或多个
            mimeMessageHelper.setTo(to);
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(text, true);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());

            //发送邮件
            mailSender.send(mimeMessageHelper.getMimeMessage());

            log.info("发送成功==================");
            return true;
        } catch (MessagingException e) {
            log.info("发送失败==================");
            log.error(e.getMessage());
            return false;
        }
    }
}
