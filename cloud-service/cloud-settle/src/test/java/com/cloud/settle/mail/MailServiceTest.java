package com.cloud.settle.mail;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import javax.annotation.Resource;

/**
 * @auther cs
 * @date 2020/4/27 15:50
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Resource
    TemplateEngine templateEngine;

    @Test
    public void sendTextEmailTest() {
        mailService.sendTextMail("721666450@qq.com", "发送文本邮件", "hello，这是Spring Boot发送的一封文本邮件!");
    }
}
