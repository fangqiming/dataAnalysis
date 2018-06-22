package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.EmailService;
import com.i000.stock.user.api.service.IndexPriceService;
import com.i000.stock.user.api.service.OffsetPriceService;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.web.config.MailSendConfig;
import javassist.bytecode.stackmap.BasicBlock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

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

    @Autowired
    private OffsetPriceService offsetPriceService;

    private SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");

    /**
     * 每天 3:35收盘的时候将价格指数信息保存到数据库中
     */
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

    @Scheduled(cron = "0 35 9 * * ?")
    public void updateAmount() {
        try {
            offsetPriceService.updateAmount();
        } catch (IOException e) {
            log.error("处理股票的拆股失败", e);
        }
    }

}
