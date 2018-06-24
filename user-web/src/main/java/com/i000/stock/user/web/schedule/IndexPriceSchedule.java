package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.EmailService;
import com.i000.stock.user.api.service.IndexPriceService;
import com.i000.stock.user.api.service.OffsetPriceService;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.web.config.MailSendConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private boolean isStockDay;

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
            setStockDay(stringBuffer);
            IndexPrice indexPrice = IndexPrice.builder().date(LocalDate.now()).content(stringBuffer.toString()).build();
            //价格保存到数据库中
            indexPriceService.save(indexPrice);
            //发送邮件推送
            if(isStockDay){
                emailService.sendFilMail(String.format("数据 %s(txt)", sd.format(new Date())), stringBuffer.toString(), mailSendConfig.isSendIndexPriceInfo());
            }
        } catch (Exception e) {
            log.error("[SAVE PRICE INDEX ERROR] e=[{}]", e);
        }
    }

    /**
     * 处理拆并股的问题
     */
    @Scheduled(cron = "0 35 9 * * ?")
    public void updateAmount() {
        try {
            offsetPriceService.updateAmount();
        } catch (IOException e) {
            log.error("处理股票的拆股失败", e);
        }
    }


    private void setStockDay(StringBuffer stringBuffer) {
        CharSequence charSequence = stringBuffer.subSequence(0, 20);
        String str = charSequence.toString();
        String[] split = str.split(",");
        LocalDate localDates = LocalDate.parse(split[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(localDates);
        isStockDay = LocalDate.now().compareTo(localDates) == 0;
    }
}
