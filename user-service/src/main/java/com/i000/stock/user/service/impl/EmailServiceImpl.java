package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.util.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

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

    @Value("${spring.mail.to}")
    private String[] to;

    @Autowired
    private JavaMailSenderImpl javaMailSender;


    @Override
    public void sendMail(String title, String content, boolean needSend) {
        if (needSend) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(title);
            message.setText(content);
            javaMailSender.send(message);
        }
    }
}
