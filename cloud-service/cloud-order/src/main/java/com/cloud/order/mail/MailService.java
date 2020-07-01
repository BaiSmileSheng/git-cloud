package com.cloud.order.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
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

    /**
     * 发送带附件的邮件
     *
     * @param to           发送对象
     * @param subject      邮件主题
     * @param content      邮件内容
     * @param file 文件列表
     * @throws MessagingException
     */
    public void sendAttachmentsMail(String to, String subject, String content, ByteArrayInputStream file, String fileName) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(from);
        Address[] tos = new InternetAddress[1];
        tos[0] = new InternetAddress(to);
        message.setRecipients(Message.RecipientType.TO,tos);
        message.setSubject(subject);
        MimeBodyPart contentPart = (MimeBodyPart) createContent(content, file,fileName);//参数为正文内容和附件流
        MimeMultipart mime = new MimeMultipart("mixed");
        mime.addBodyPart(contentPart);
        message.setContent(mime);
        javaMailSender.send(message);
    }

    /**
     * io流转成附件
     * @param content
     * @param file
     * @param fileName
     * @return
     * @throws Exception
     */
    private Part createContent(String content, ByteArrayInputStream file, String fileName) throws Exception{
        MimeMultipart contentMultipart = new MimeMultipart("related");
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html;charset=UTF-8");
        contentMultipart.addBodyPart(htmlPart);
        //附件部分
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeBodyPart excelBodyPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(file, "application/msexcel");
        DataHandler dataHandler = new DataHandler(dataSource);
        excelBodyPart.setDataHandler(dataHandler);
        excelBodyPart.setFileName(MimeUtility.encodeText(fileName));
        contentMultipart.addBodyPart(excelBodyPart);
        contentPart.setContent(contentMultipart);
        return contentPart;

    }
}
