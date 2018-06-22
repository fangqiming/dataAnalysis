package com.i000.stock.user.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:59 2018/6/16
 * @Modified By:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailSendConfig {
    private boolean sendSuccessNotice;
    private boolean sendFailNotice;
    private boolean sendIndexPriceInfo;


}


