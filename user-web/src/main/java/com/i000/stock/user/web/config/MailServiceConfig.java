package com.i000.stock.user.web.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:00 2018/6/22
 * @Modified By:
 */
@Data
@Component
public class MailServiceConfig {

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.encoding}")
    private String encoding;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private Integer timeout;
    @Value("${spring.mail.properties.mail.smtp.port}")
    private Integer port;
    @Value("${spring.mail.properties.mail.smtp.socketFactory}")
    private String socketFactory;

}
