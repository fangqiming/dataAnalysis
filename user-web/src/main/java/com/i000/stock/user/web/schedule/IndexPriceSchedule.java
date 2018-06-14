package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.IndexPriceService;
import com.i000.stock.user.dao.model.IndexPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

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

    @Scheduled(cron = "0 35 15 * * ?")
    public void saveIndexPrice() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            IndexPrice indexPrice = IndexPrice.builder().date(LocalDate.now()).content(stringBuffer.toString()).build();
            indexPriceService.save(indexPrice);
        } catch (Exception e) {
            log.error("[SAVE PRICE INDEX ERROR] e=[{}]", e);
        }

    }
}
