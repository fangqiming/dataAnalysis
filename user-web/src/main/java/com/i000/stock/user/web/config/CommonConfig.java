package com.i000.stock.user.web.config;

import com.i000.stock.user.api.entity.bo.AssetInitBo;
import com.i000.stock.user.api.entity.bo.TokenBo;
import com.i000.stock.user.core.file.oss.OSSFileUpload;
import com.i000.stock.user.core.file.oss.OSSUtil;
import com.tictactec.ta.lib.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 20:04 2018/4/27
 * @Modified By:
 */
@Configuration
public class CommonConfig {
    @Bean("imageOSSFileUpload")
    @ConfigurationProperties(prefix = "component.file.system.oss.private")
    public OSSFileUpload imageOSSFileUpload(@Value("${component.file.system.oss.image.catalog}") String catalog,
                                            @Value("${component.file.system.oss.http-prefix}") String httpPrefix) {
        OSSFileUpload imageOSSFileUpload = new OSSFileUpload();
        imageOSSFileUpload.setCatalog(catalog);
        imageOSSFileUpload.setHttpPrefix(httpPrefix);
        return imageOSSFileUpload;
    }

    @Bean
    @ConfigurationProperties(prefix = "component.file.system.oss.private")
    public OSSUtil ossUtil(@Value("${component.file.system.oss.image.catalog}") String catalog,
                           @Value("${component.file.system.oss.private.read-endpoint}") String endpoint) {
        OSSUtil ossUtil = new OSSUtil();
        ossUtil.setCatalog(catalog);
        ossUtil.setEndpoint(endpoint);
        return ossUtil;
    }

    @Bean
    public AssetInitBo assetInitBo() {
        return new AssetInitBo();
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor result = new ThreadPoolTaskExecutor();
        result.setMaxPoolSize(6);
        result.setCorePoolSize(2);
        result.setKeepAliveSeconds(180);
        result.setQueueCapacity(6);
        return result;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private MailServiceConfig mailServiceConfig;

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailServiceConfig.getHost());
        javaMailSender.setUsername(mailServiceConfig.getUsername());
        javaMailSender.setPassword(mailServiceConfig.getPassword());
        javaMailSender.setDefaultEncoding(mailServiceConfig.getEncoding());
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", mailServiceConfig.getAuth());
        properties.put("mail.smtp.timeout", mailServiceConfig.getTimeout());
        properties.put("mail.smtp.port", mailServiceConfig.getPort());
        properties.put("mail.smtp.socketFactory.class", mailServiceConfig.getSocketFactory());
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }


    @Bean
    @ConfigurationProperties(prefix = "jq.data")
    public TokenBo tokenBo() {
        return new TokenBo();
    }

    @Bean
    public Core core() {
        return new Core();
    }
}
