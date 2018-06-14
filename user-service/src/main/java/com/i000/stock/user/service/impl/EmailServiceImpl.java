package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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

    @Override
    public void sendMail(String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        sender.send(message);

        System.out.println("邮件发送成功");
    }
}
