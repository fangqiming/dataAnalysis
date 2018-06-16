package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.EmailService;
import com.i000.stock.user.api.service.IndexPriceService;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.web.config.MailSendConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:08 2018/6/14
 * @Modified By:
 */
@Slf4j
@Component
public class IndexPriceSchedule {

    @Resource
    private IndexPriceService indexPriceService;

    @Resource
    private EmailService emailService;

    @Autowired
    private MailSendConfig mailSendConfig;

    private SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

    @Scheduled(cron = "0 35 15 * * ?")
    public void saveIndexPrice() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            IndexPrice indexPrice = IndexPrice.builder().date(LocalDate.now()).content(stringBuffer.toString()).build();
            //价格保存到数据库中
            indexPriceService.save(indexPrice);
            //发送邮件推送
            emailService.sendFilMail(String.format("数据 %s(txt)", sd.format(new Date())), stringBuffer.toString(), mailSendConfig.isSendIndexPriceInfo());
        } catch (Exception e) {
            log.error("[SAVE PRICE INDEX ERROR] e=[{}]", e);
        }
    }

}
