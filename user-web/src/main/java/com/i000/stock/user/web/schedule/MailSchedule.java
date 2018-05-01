package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.MailFetchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:26 2018/4/27
 * @Modified By:
 */
@Slf4j
@Component
public class MailSchedule {

    @Resource
    private MailFetchService mailFetchService;

    @Resource
    private AssetService assetService;

    @Scheduled(cron = "0 30 15 * * ?")
    public void fetchMail() throws Exception {
        LocalDate localDate = mailFetchService.initMail();
        if(Objects.nonNull(localDate)){
            assetService.calculate(localDate);
        }
    }
}
