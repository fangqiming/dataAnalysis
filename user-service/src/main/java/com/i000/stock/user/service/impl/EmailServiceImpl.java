package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:31 2018/6/14
 * @Modified By:
 */
@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.username}")
    private String[] to;

    @Autowired
    private JavaMailSender sender;

    private SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void sendMail(String title, String content, boolean needSend) {
        if (needSend) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(title);
            message.setText(content);
            sender.send(message);
        }
    }

    @Override
    public void sendFilMail(String title, String content, boolean needSend) {
        if (needSend) {
            MimeMessage mimeMessage = sender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(title);
                InputStreamSource source = () -> new ByteArrayInputStream(content.getBytes());
                helper.addAttachment(String.format("%s.txt", sd.format(new Date())), source);
                sender.send(mimeMessage);
            } catch (Exception e) {
                log.error("[SEND PRICE INDEX INFO ERROR] e=[{}]", e);
                sendMail("【千古:价格指数邮件推送失败】", e.toString(), true);
            }
        }
    }
}
