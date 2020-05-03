package com.cloud.order.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @auther cs
 * @date 2020/4/27 15:49
 * @description
 */
@Service
public class MailService {
    @Value("${spring.mail.username}")
    private String from;

    @Autowired // 项目启动时将mailSender注入
    private JavaMailSender javaMailSender;

    /**
     * 发送文本邮件
     *
     * @param to      发送对象
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendTextMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(from); // 邮件的发起者

        javaMailSender.send(message);
    }

    /**
     * 发送HTMl邮件
     *
     * @param to      发送对象
     * @param subject 邮件主题
     * @param content 邮件内容
     * @throws MessagingException
     */
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    /**
     * 发送带附件的邮件
     *
     * @param to           发送对象
     * @param subject      邮件主题
     * @param content      邮件内容
     * @param filePathList 文件列表
     * @throws MessagingException
     */
    public void sendAttachmentMail(String to, String subject, String content, String[] filePathList) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        for (String filePath : filePathList) {
            FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
            String fileName = fileSystemResource.getFilename();
            helper.addAttachment(fileName, fileSystemResource);
        }

        javaMailSender.send(message);
    }
}
