//package com.i000.stock.user.service.impl.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import javax.mail.*;
//import java.util.Properties;
//
///**
// * @Author:qmfang
// * @Description:
// * @Date:Created in 12:30 2018/4/27
// * @Modified By:
// */
//@Data
//@Configuration
//@ConfigurationProperties(prefix = "mail")
//public class MailConfigure {
//
//    private String host;
//    private String user;
//    private String password;
//
//    /**
//     * 监控人的邮箱地址
//     */
//    private String monitor;
//
//    Message[] messages;
//    Properties properties;
//    Session session;
//    Store store;
//
//    public void init() throws MessagingException {
//        properties = new Properties();
//        properties.setProperty("mail.pop3.host", "pop.qq.com");
//        properties.setProperty("mail.pop3.port", "995");
//        properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        properties.setProperty("mail.pop3.socketFactory.fallback", "true");
//        properties.setProperty("mail.pop3.socketFactory.port", "995");
//        session = Session.getDefaultInstance(properties, null);
//        store = session.getStore("pop3");
//        store.connect(host, user, password);
//    }
//
//    public Message[] createMessage() throws MessagingException {
//        Folder folder = store.getFolder("INBOX");
//        folder.open(Folder.READ_ONLY);
//        messages = folder.getMessages();
//        return messages;
//    }
//
//}
